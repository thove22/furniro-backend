package com.furniro.backend.service;

import com.furniro.backend.domain.cart.Cart;
import com.furniro.backend.domain.cart.CartResponseDTO;
import com.furniro.backend.domain.cart_item.CartItem;
import com.furniro.backend.domain.cart_item.CartItemRequestDTO;
import com.furniro.backend.domain.cart_item.CartItemResponseDTO;
import com.furniro.backend.domain.products.Product;
import com.furniro.backend.domain.users.User;
import com.furniro.backend.exception.ResourceNotFoundException;
import com.furniro.backend.repository.CartItemRepository;
import com.furniro.backend.repository.CartRepository;
import com.furniro.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart getOrCreateCart(User user){
        Optional<Cart> cart =  cartRepository.findByUserId(user.getId());

        if(cart.isPresent()){
            return cart.get();
        }

        Cart newCart = new Cart();
        newCart.setUser(user);

        return cartRepository.save(newCart);
    }
    public CartResponseDTO getCart(User user){
        Cart cart = getOrCreateCart(user);
        return toCartResponse(cart);
    }
    public CartResponseDTO toCartResponse(Cart cart){
        List<CartItemResponseDTO> items = cart.getItems().stream()
                .map(item -> {
                    BigDecimal unitPrice = item.getProduct().getPrice();
                    BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                    return new CartItemResponseDTO(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        unitPrice,
                        item.getQuantity(),
                        lineTotal
                );
                }).toList();

                BigDecimal total = items.stream()
                .map(CartItemResponseDTO::linetotal)
                 .reduce(BigDecimal.ZERO, BigDecimal::add);
                return new CartResponseDTO(cart.getId(), items, total);
    }

    public CartResponseDTO addItem(User user, CartItemRequestDTO requestDTO){

            Cart cart =  getOrCreateCart(user);
            Product product = productRepository.findById(requestDTO.productId()).
                    orElseThrow(()-> new ResourceNotFoundException(
                            "Product not found with id: " + requestDTO.productId()
            ));

           Optional<CartItem> existing = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

            if(existing.isPresent()){
                CartItem item = existing.get();
                item.setQuantity(item.getQuantity() + requestDTO.quantity());
                cartItemRepository.save(item);
            }else{
                CartItem item = new CartItem();
                item.setCart(cart);
                item.setProduct(product);
                item.setQuantity(requestDTO.quantity());
                cartItemRepository.save(item);
            }

            return getCart(user);
    }

    public CartResponseDTO updateItemQuantity(User user, Long itemId, Integer quantity){
        if(quantity == null || quantity <= 0){
            throw new IllegalArgumentException("Quantity must be posetive");
        }

        CartItem item = cartItemRepository.findById(itemId).orElseThrow(
                ()-> new ResourceNotFoundException("Card item not found "+ itemId)
        );

        if(!item.getCart().getUser().getId().equals(user.getId())){
            throw new ResourceNotFoundException("Card Item not found: " + itemId);
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        return getCart(user);
    }
}
