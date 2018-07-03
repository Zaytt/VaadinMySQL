package com.example.VaadinMySQL.ui;

import com.example.VaadinMySQL.model.Customer;
import com.example.VaadinMySQL.service.CustomerService;
import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomerForm extends com.vaadin.ui.FormLayout{

    private Binder<Customer> binder = new Binder<>(Customer.class);

    // Vaadin components to be used
    private TextField firstName = new TextField("First name");
    private TextField lastName = new TextField("Last name");
    private TextField email = new TextField("Email");
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");

    // References to the currently edited customer
    private Customer customer;
    private VaadinUI myUI;

    public CustomerForm(VaadinUI myUI) {
        this.myUI = myUI;

        setSizeUndefined();
        HorizontalLayout buttons = new HorizontalLayout(save, delete);
        addComponents(firstName, lastName, email, buttons);
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        save.addClickListener(e -> this.save());
        delete.addClickListener(e -> this.delete());

        binder.bindInstanceFields(this);
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        binder.setBean(customer);

        // Show delete button for only customers already in the database
        delete.setVisible(customer.isPersisted());

        setVisible(true);
        firstName.selectAll();
    }

    public Customer getCustomer(){
        return this.customer;
    }

    private void delete() {
        //TODO show an alert when trying to delete, DataIntegrityException
        myUI.getCustomerService().delete(customer);
        myUI.updateGrid();
        setVisible(false);
    }

    //TODO switch between save and update a Customer
    private void save() {
        //TODO show a message that customer was added
        myUI.getCustomerService().save(customer);
        myUI.updateGrid();
        setVisible(false);
    }
}

