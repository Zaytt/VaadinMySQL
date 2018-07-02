package com.example.VaadinMySQL.service;

import com.example.VaadinMySQL.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class CustomerService {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<Customer> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM customer",
                (rs, rowNum) -> new Customer(
                        rs.getLong("customer_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getBoolean("active"),
                        rs.getString("create_date")));
    }

    public void update(Customer customer) {
        jdbcTemplate.update(
                "UPDATE customer SET   first_name=:first_name, " +
                                            "last_name=:last_name, " +
                                            "email=:email, " +
                                            "active=:active, " +
                                            "create_date=:create_date " +
                        "WHERE customer_id=:id",
                new HashMap() {{
                    put("first_name", customer.getFirstName());
                    put("last_name", customer.getLastName());
                    put("email", customer.getEmail());
                    put("active", customer.isActive());
                    put("create_date", customer.getJoinDate());
                    put("id", customer.getId());
                }});

//                customer.getFirstName(), customer.getLastName(), customer.getEmail(),
//                customer.isActive(), customer.getJoinDate(), customer.getId());
    }
}
