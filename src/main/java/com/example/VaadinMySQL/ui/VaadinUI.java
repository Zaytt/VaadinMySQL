package com.example.VaadinMySQL.ui;

import com.example.VaadinMySQL.model.Customer;
import com.example.VaadinMySQL.service.CustomerService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@SpringUI
public class VaadinUI extends UI {

    @Autowired
    private CustomerService customerService;

    //Grid listing the customers
    private Grid<Customer> customerGrid = new Grid<>(Customer.class);
    //Text field that works as a filter
    private TextField filterField = new TextField();
    //Side form to add, edit and delete customers
    private CustomerForm form = new CustomerForm(this);
    //Toolbar on top of grid to filter and add customers
    private HorizontalLayout toolbar;

    @Override
    protected void init(VaadinRequest request) {
        Layout layout = buildUI();
        setContent(layout);
    }

    private Layout buildUI(){
        //Create the general vertical layout of the UI
        final VerticalLayout layout = new VerticalLayout();

        //Add the components to the layout
        layout.addComponents(buildTopToolbar(), buildMainLayout(), buildBottomToolBar());

        // fetch list of Customers from service and assign it to Grid
        this.updateGrid();

        form.setVisible(false);

        return layout;
    }

    private HorizontalLayout buildMainLayout(){
        //Build the grid
        buildGrid();

        //Add the form and grid to the layout
        HorizontalLayout main = new HorizontalLayout(customerGrid, form);

        //Configure the grid and layout size
        main.setSizeFull();
        customerGrid.setSizeFull();
        main.setExpandRatio(customerGrid, 1);

        return main;
    }

    /**
     * Configures the Grid for the Customers table
     */
    private Grid buildGrid(){
        //Set the grids columns and add a listener for when selecting a customer
        updateGrid();
        customerGrid.setColumns("firstName", "lastName", "email", "joinDate");
        customerGrid.asSingleSelect().addValueChangeListener(event -> selectRow());

        return customerGrid;
    }

    /**
     * Builds a toolbar that contains:
     * Text field for filtering
     * Button for clearing the filter
     * Button for adding a new element to the grid
     * @return The built toolbar as an horizontal layout
     */
    private HorizontalLayout buildTopToolbar(){
        //Set the text field as filter
        filterField.setPlaceholder("Filter by name...");
        filterField.addValueChangeListener(e -> updateGrid());
        filterField.setValueChangeMode(ValueChangeMode.LAZY);

        //Add clear button next to the text field
        Button clearFilterButton = new Button(VaadinIcons.CLOSE);
        clearFilterButton.setDescription("Clear the current filter");
        clearFilterButton.addClickListener(e -> clearFilter());

        // Add add customer button
        Button addCustomerButton = new Button("New customer");
        addCustomerButton.setIcon(VaadinIcons.PLUS_CIRCLE_O);
        addCustomerButton.addClickListener(event -> {
            customerGrid.asSingleSelect().clear();
            form.setCustomer(new Customer());
            form.show();
        });

        // Create a CSS Layout
        CssLayout filteringLayout = new CssLayout();
        filteringLayout.addComponents(filterField, clearFilterButton, addCustomerButton);
        filteringLayout.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        // Make the toolbar
        this.toolbar = new HorizontalLayout(filteringLayout);

        return this.toolbar;
    }

    private HorizontalLayout buildBottomToolBar(){
        // Add a print pdf button
        Button pdfButton = new Button("PDF");
        pdfButton.setIcon(VaadinIcons.FILE_TEXT_O);
        pdfButton.setStyleName("danger");
        pdfButton.addClickListener(event -> {
            //TODO function to print a PDF Report
        });

        // Add a generate excel file button
        Button excelButton = new Button("Excel");
        excelButton.setIcon(VaadinIcons.FILE_TABLE);
        excelButton.setStyleName("friendly");
        excelButton.addClickListener(event -> {
            //TODO function to print an Excel file
        });

        // Make the toolbar
        HorizontalLayout bottomToolbar = new HorizontalLayout(pdfButton, excelButton);

        return bottomToolbar;
    }

    /**
     * Sets the customer property from the CustomerForm object to the one selected in the grid
     */
    private void selectRow() {
        if (customerGrid.asSingleSelect().isEmpty()) {
            form.setVisible(false);
        } else {
            form.setCustomer(customerGrid.asSingleSelect().getValue());
            form.show();
        }
    }

    public void updateGrid() {
        List<Customer> customers = customerService.findAll(this.filterField.getValue());
        customerGrid.setItems(customers);
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public void setFilterFieldText(String text){
        this.filterField.setValue(text);
    }

    public void clearFilter(){
        this.filterField.clear();
    }

    public void showError(String caption, String description){
        new Notification(caption,
                description,
                Notification.Type.ERROR_MESSAGE, true)
                .show(Page.getCurrent());
    }

    public void showMessage(String caption, String description){
        new Notification(caption,
                description,
                Notification.Type.TRAY_NOTIFICATION, true)
                .show(Page.getCurrent());
    }

}
