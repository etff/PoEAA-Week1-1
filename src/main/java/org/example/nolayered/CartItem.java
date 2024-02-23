package org.example.nolayered;

public class CartItem {
    private Long id;
    private Long productId;
    private Long optionId;
    private Integer quantity;
    private Long cartId;

    public CartItem(Long id, Long productId, Long optionId, Integer quantity, Long cartId) {
        this.id = id;
        this.productId = productId;
        this.optionId = optionId;
        this.quantity = quantity;
        this.cartId = cartId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }
}
