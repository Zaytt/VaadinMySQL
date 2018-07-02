package com.example.VaadinMySQL.ui;

import com.example.VaadinMySQL.model.Customer;
import com.example.VaadinMySQL.service.CustomerService;
import com.vaadin.data.Binder;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@SpringUI
public class VaadinUI extends UI {

    @Autowired
    private CustomerService customerService;

    private Customer customer;
    private Binder<Customer> binder = new Binder<>(Customer.class);

    private Grid<Customer> customerGrid = new Grid<>(Customer.class);
    private TextField firstName = new TextField("First name");
    private TextField lastName = new TextField("Last name");
    private Button save = new Button("Save", e -> saveCustomer());


    @Override
    protected void init(VaadinRequest request) {
        updateGrid();
        customerGrid.setColumns("firstName", "lastName");
        customerGrid.addSelectionListener(e -> updateForm());

        binder.bindInstanceFields(this);

        VerticalLayout layout = new VerticalLayout(customerGrid, firstName, lastName, save);
        setContent(layout);

    }

    private void setFormVisible(boolean visible) {
        firstName.setVisible(visible);
        lastName.setVisible(visible);
        save.setVisible(visible);
    }

    private void updateForm() {
        if (customerGrid.asSingleSelect().isEmpty()) {
            setFormVisible(false);
        } else {
            customer = customerGrid.asSingleSelect().getValue();
            binder.setBean(customer);
            setFormVisible(true);
        }
    }

    private void updateGrid() {
        List<Customer> customers = customerService.findAll();
        customerGrid.setItems(customers);
        setFormVisible(false);
    }

    private void saveCustomer() {
        customerService.update(customer);
        updateGrid();
    }
}
