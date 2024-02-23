package org.example.nolayered;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.sql.PreparedStatement;
import java.util.Map;

@RestController
public class CartController {
    private final JdbcTemplate jdbcTemplate;
    private KeyHolder keyHolder;

    public CartController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        keyHolder = new GeneratedKeyHolder();
    }

    @PostMapping("/carts")
    @Transactional
    public ResponseEntity<Long> addProduct(@RequestBody Map<String, Object> commMap) {
        Long cartId = null;
        if (commMap.get("cartId") == null) {
            jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO CART DEFAULT VALUES", new String[] {"id"});
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            cartId = key != null ? key.longValue() : -1;
            jdbcTemplate.update("INSERT INTO CART_ITEM (cart_id, product_id, option_id, quantity) VALUES (?, ?, ?, ?)",
                    cartId, commMap.get("productId"), commMap.get("optionId"), commMap.get("quantity"));
        } else {
            Cart cart = jdbcTemplate.queryForObject("SELECT * FROM CART WHERE id = ?", cartMapper, commMap.get("cartId"));

            // cart exist
            if (cart != null) {
                CartItem cartItem = jdbcTemplate.queryForObject("SELECT * FROM CART_ITEM WHERE cart_id = ? AND product_id = ? AND option_id = ?",
                        cartItemMapper, cart.getId(), commMap.get("productId"), commMap.get("optionId"));
                // cart item exist
                if (cartItem != null) {
                    jdbcTemplate.update("UPDATE CART_ITEM SET quantity = ? WHERE id = ?", cartItem.getQuantity() + (int) commMap.get("quantity"), cartItem.getId());
                } else {
                    jdbcTemplate.update("INSERT INTO CART_ITEM (cart_id, product_id, option_id, quantity) VALUES (?, ?, ?, ?)",
                            cart.getId(), commMap.get("productId"), commMap.get("optionId"), commMap.get("quantity"));
                }
            } else {
                jdbcTemplate.update(conn -> {
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO CART DEFAULT VALUES", new String[] {"id"});
                    return ps;
                }, keyHolder);

                Number key = keyHolder.getKey();
                cartId = key != null ? key.longValue() : -1;
                jdbcTemplate.update("INSERT INTO CART_ITEM (cart_id, product_id, option_id, quantity) VALUES (?, ?, ?, ?)",
                        cartId, commMap.get("productId"), commMap.get("optionId"), commMap.get("quantity"));
            }
        }
        return ResponseEntity.created(URI.create("/carts/" + cartId)).build();
    }

    static RowMapper<Cart> cartMapper = (rs, rowNum) -> new Cart(rs.getLong("id"));
    static RowMapper<CartItem> cartItemMapper = (rs, rowNum) -> new CartItem(
            rs.getLong("id"),
            rs.getLong("product_id"),
            rs.getLong("option_id"),
            rs.getInt("quantity"),
            rs.getLong("cart_id")
    );
}
