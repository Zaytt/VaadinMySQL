package com.example.VaadinMySQL.service;

import com.example.VaadinMySQL.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedTemplate;


    public List<Customer> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM customer",
                (rs, rowNum) -> new Customer(
                        rs.getLong("customer_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("create_date")));
    }

    public void save(Customer customer){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String currentDate = sdf.format(date);
        namedTemplate.update("INSERT INTO `sakila`.`customer` (`store_id`, `first_name`, `last_name`, `email`, " +
                                             "`address_id`, `active`, `create_date`) " +
                "VALUES ('1', :first_name, :last_name , :email, '605', '1', :create_date);",
                new HashMap() {{
                    put("first_name", customer.getFirstName());
                    put("last_name", customer.getLastName());
                    put("email", customer.getEmail());
                    put("create_date", currentDate);
                }});
    }

    public void update(Customer customer) {
        namedTemplate.update(
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
                    put("create_date", customer.getJoinDate());
                    put("id", customer.getId());
                }});

    }

    public void delete(Customer customer){
        jdbcTemplate.update("DELETE FROM customer " +
                                 "WHERE customer_id = ?",
                customer.getId());
    }
}
