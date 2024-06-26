package view;

import business.BookManager;
import business.BrandManager;
import business.CarManager;
import business.ModelManager;
import core.ComboItem;
import core.Helper;
import entity.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.ArrayList;

public class AdminView extends Layout {
    private JPanel container;
    private JLabel lbl_welcome;
    private JPanel pnl_top;
    private JTabbedPane tab_menu_car;
    private JButton btn_logout;
    private JPanel pnl_brand;
    private JScrollPane scl_brand;
    private JTable tbl_brand;
    private JPanel pnl_model;
    private JScrollPane scrl_model;
    private JTable tbl_model;
    private JLabel lbl_src_brand;
    private JComboBox cmb_s_model_brand;
    private JComboBox cmb_s_model_type;
    private JComboBox cmb_s_model_fuel;
    private JComboBox cmb_s_model_gear;
    private JButton btn_search_model;
    private JTabbedPane tab_menu;
    private JLabel lbl_src_type;
    private JLabel lbl_src_fuel;
    private JLabel lbl_src_gear;
    private JButton btn_clear_src;
    private JPanel pnl_car;
    private JTable tbl_car;
    private JPanel pnl_carr;
    private JScrollPane scr_car;
    private JTable tbl_book;
    private JFormattedTextField fld_strt_date;
    private JFormattedTextField fld_fnsh_date;
    private JComboBox cmb_booking_gear;
    private JComboBox cmb_booing_fuel;
    private JComboBox cmb_booking_type;
    private JButton btn_booking_search;
    private JLabel lbl_book_start;
    private JLabel lbl_finish_date;
    private JLabel lbl_book_gear;
    private JLabel lbl_book_fuel;
    private JLabel lbl_book_type;
    private JButton btn_booing_cancel;
    private JPanel pnl_book;
    private JPanel pnl_rented;
    private JTable tbl_rented;
    private JScrollPane scrl_rented;
    private JComboBox cmb_rented_plate;
    private JButton btn_search_rented;
    private JButton btn_cancel_rented;
    private User user;
    private DefaultTableModel tmdl_rented = new DefaultTableModel();
    private DefaultTableModel tmdl_brand = new DefaultTableModel();
    private DefaultTableModel tmdl_model = new DefaultTableModel();
    private DefaultTableModel tmdl_car = new DefaultTableModel();
    private DefaultTableModel tmdl_booking = new DefaultTableModel();
    private BrandManager brandManager;
    private ModelManager modelManager;
    private JPopupMenu brand_menu;
    private JPopupMenu model_menu;
    private JPopupMenu car_menu;
    private JPopupMenu rented_menu;
    private Object[] col_model;
    private CarManager carManager;
    private JPopupMenu booking_menu;
    private Object[] col_car;
    private Object[] col_rented;
    private BookManager bookManager;

    public AdminView(User user) {
        this.carManager = new CarManager();
        this.modelManager = new ModelManager();
        this.brandManager = new BrandManager();
        this.bookManager =new BookManager();
        this.add(container);
        this.guiInitilaze(1000, 500);
        this.user = user;

        if (this.user == null) {
            dispose();
        }

        this.lbl_welcome.setText("Hoşgeldiniz " + this.user.getUsername());

        SystemExit();

        loadBrandTable();
        loadBrandComponent();


        loadModelTable(null);
        loadModelComponent();
        loadModelFilter();

        loadCarTable();
        loadCarComponent();

        loadBookingTable(null);
        loadBookingComponent();
        loadBookingFilter();


        loadRentedTable(null);
        loadRentedComponent();
        loadRentedFilter();


    }

    public void loadRentedTable(ArrayList<Object[]> bookList){
        col_rented = new Object[]{"ID","Plaka","Araç Marka","Araç Model","Müşteri","Telefon","Mail","TC NO","Başlangıç Tarihi","Bitiş Tarihi","Fiyat" };
        if(bookList == null){
            bookList=this.bookManager.getForTable(col_rented.length,this.bookManager.findAll());
        }
        createTable(this.tmdl_rented,this.tbl_rented,col_rented, bookList);
    }
    public void loadRentedComponent() {
        this.rented_menu = new JPopupMenu();
        this.rented_menu.add("İptal Et").addActionListener(e -> {
            if (Helper.confirm("sure")) {
                int selectBookId = this.getTableSelectedRow(this.tbl_rented, 0);

                if (this.bookManager.delete(selectBookId)) {
                    Helper.showMsg("done");
                    loadRentedTable(null);
                } else {
                    Helper.showMsg("error");
                }
            }
        });
        this.rented_menu.setComponentPopupMenu(rented_menu);
        tableRowSelect(this.tbl_rented,rented_menu);
        btn_search_rented.addActionListener(e -> {
            ComboItem selectedCar = (ComboItem) this.cmb_rented_plate.getSelectedItem();
            int carId = 0;
            if(selectedCar != null){
                carId =selectedCar.getKey();
            }
            ArrayList<Book> bookListBySearch = this.bookManager.searchForTable(carId);
            ArrayList<Object[]> bookRowListBySearch = this.bookManager.getForTable(this.col_rented.length, bookListBySearch);
            loadRentedTable(bookRowListBySearch);
        });
        btn_cancel_rented.addActionListener(e -> {
            loadRentedFilter();
        });

    }
    public void loadRentedFilter() {
        this.cmb_rented_plate.removeAllItems();
        for (Car obj : this.carManager.findAll()) {
            this.cmb_rented_plate.addItem(new ComboItem(obj.getId(), obj.getPlate()));
        }
        this.cmb_rented_plate.setSelectedItem(null);
    }


    public void loadBookingComponent() {
        this.booking_menu = new JPopupMenu();
        tableRowSelect(this.tbl_book, booking_menu);

        this.booking_menu.add("Rezervasyon Yap").addActionListener(e -> {
            int selectedCarId =this.getTableSelectedRow(this.tbl_book,0);
            BookingView bookingView = new BookingView(
                    this.carManager.getById(selectedCarId),
                    this.fld_strt_date.getText(),
                    this.fld_fnsh_date.getText()
            );
            bookingView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadBookingTable(null);
                    loadBookingFilter();
                }
            });
        });
        this.tbl_book.setComponentPopupMenu(booking_menu);

        btn_booking_search.addActionListener(e -> {
            ArrayList<Car> carList = this.carManager.searchForBooking(
                    fld_strt_date.getText(),
                    fld_fnsh_date.getText(),
                    (Model.Type) cmb_booking_type.getSelectedItem(),
                    (Model.Gear) cmb_booking_gear.getSelectedItem(),
                    (Model.Fuel) cmb_booing_fuel.getSelectedItem()
            );
            ArrayList<Object[]> carBookingRow = this.carManager.getForTable(this.col_car.length, carList);
            loadBookingTable(carBookingRow);
        });
        btn_booing_cancel.addActionListener(e -> {
            loadBookingFilter();
        });
    }

    private void loadBookingTable(ArrayList<Object[]> carList) {
        Object[] col_booking_list = {"ID", "Marka", "Model", "Plaka", "Renk", "KM", "Yıl", "Tip", "Yakıt Türü", "Vites"};
        createTable(this.tmdl_booking, this.tbl_book, col_booking_list, carList);
    }

    public void loadBookingFilter() {
        this.cmb_booking_type.setModel(new DefaultComboBoxModel<>(Model.Type.values()));
        this.cmb_booking_type.setSelectedItem(null);
        this.cmb_booking_gear.setModel(new DefaultComboBoxModel<>(Model.Gear.values()));
        this.cmb_booking_gear.setSelectedItem(null);
        this.cmb_booing_fuel.setModel(new DefaultComboBoxModel<>(Model.Fuel.values()));
        this.cmb_booing_fuel.setSelectedItem(null);
    }

    public void loadCarTable() {
        col_car = new Object[]{"ID", "Marka", "Model", "Plaka", "Renk", "KM", "Yıl", "Tip", "Yakıt Türü", "Vites"};
        ArrayList<Object[]> carList = this.carManager.getForTable(col_car.length, this.carManager.findAll());
        createTable(this.tmdl_car, this.tbl_car, col_car, carList);
    }

    public void loadCarComponent() {
        this.car_menu = new JPopupMenu();
        tableRowSelect(this.tbl_car, car_menu);

        this.car_menu.add("Yeni").addActionListener(e -> {
            CarView carView = new CarView(new Car());
            carView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCarTable();
                }
            });
        });
        this.car_menu.add("Güncelle").addActionListener(e -> {
            int selectedModelId = this.getTableSelectedRow(tbl_car, 0);
            CarView carView = new CarView(this.carManager.getById(selectedModelId));
            carView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCarTable();
                }
            });
        });
        this.car_menu.add("Sil").addActionListener(e -> {
            if (Helper.confirm("sure")) {
                int selectedModelId = this.getTableSelectedRow(tbl_car, 0);
                if (this.carManager.delete(selectedModelId)) {
                    Helper.showMsg("done");
                    loadCarTable();
                } else {
                    Helper.showMsg("error");
                }
            }
        });
    }

    private void loadModelComponent() {
        this.model_menu = new JPopupMenu();
        tableRowSelect(this.tbl_model, this.model_menu);

        this.model_menu.add("Yeni").addActionListener(e -> {
            ModelView modelView = new ModelView(new Model());
            modelView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadModelTable(null);
                }
            });
        });
        this.model_menu.add("Güncelle").addActionListener(e -> {
            int selectedModelId = this.getTableSelectedRow(tbl_model, 0);
            ModelView modelView = new ModelView(this.modelManager.getById(selectedModelId));
            modelView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadModelTable(null);
                }
            });
        });
        this.model_menu.add("Sil").addActionListener(e -> {
            if (Helper.confirm("sure")) {
                int selectedModelId = this.getTableSelectedRow(tbl_model, 0);
                if (this.modelManager.delete(selectedModelId)) {
                    Helper.showMsg("done");
                    loadModelTable(null);
                } else {
                    Helper.showMsg("error");
                }
            }
        });

        this.tbl_model.setComponentPopupMenu(model_menu);

        this.btn_search_model.addActionListener(e -> {
            ComboItem selectedBrand = (ComboItem) this.cmb_s_model_brand.getSelectedItem();
            int brandId = 0;
            if (selectedBrand != null) {
                brandId = selectedBrand.getKey();
            }
            ArrayList<Model> modelListBySearch = this.modelManager.searchForTable(
                    brandId,
                    (Model.Fuel) cmb_s_model_fuel.getSelectedItem(),
                    (Model.Gear) cmb_s_model_gear.getSelectedItem(),
                    (Model.Type) cmb_s_model_type.getSelectedItem()

            );
            ArrayList<Object[]> modelRowListBySearch = this.modelManager.getForTable(this.col_model.length, modelListBySearch);
            loadModelTable(modelRowListBySearch);
        });

        this.btn_clear_src.addActionListener(e -> {
            this.cmb_s_model_type.setSelectedItem(null);
            this.cmb_s_model_gear.setSelectedItem(null);
            this.cmb_s_model_fuel.setSelectedItem(null);
            this.cmb_s_model_brand.setSelectedItem(null);
            loadModelTable(null);
        });
    }

    public void loadModelTable(ArrayList<Object[]> modelList) {
        this.col_model = new Object[]{"Model ID", "Marka", "Model Adı", "Tip", "Yıl", "Yakıt Türü", "Vites"};
        if (modelList == null) {
            modelList = this.modelManager.getForTable(this.col_model.length, this.modelManager.findAll());
        }
        createTable(this.tmdl_model, this.tbl_model, this.col_model, modelList);
    }

    public void loadModelFilter() {
        this.cmb_s_model_type.setModel(new DefaultComboBoxModel<>(Model.Type.values()));
        this.cmb_s_model_type.setSelectedItem(null);
        this.cmb_s_model_gear.setModel(new DefaultComboBoxModel<>(Model.Gear.values()));
        this.cmb_s_model_gear.setSelectedItem(null);
        this.cmb_s_model_fuel.setModel(new DefaultComboBoxModel<>(Model.Fuel.values()));
        this.cmb_s_model_fuel.setSelectedItem(null);
        loadModelFilterBrand();
    }

    public void loadModelFilterBrand() {
        this.cmb_s_model_brand.removeAllItems();
        for (Brand obj : brandManager.findAll()) {
            this.cmb_s_model_brand.addItem(new ComboItem(obj.getId(), obj.getName()));
        }
        this.cmb_s_model_brand.setSelectedItem(null);
    }

    public void loadBrandTable() {
        Object[] col_brand = {"Marka ID", "Marka Adı"};
        ArrayList<Object[]> brandList = this.brandManager.getForTable(col_brand.length);
        this.tmdl_brand.setColumnIdentifiers(col_brand);
        this.createTable(this.tmdl_brand, this.tbl_brand, col_brand, brandList);
    }

    public void loadBrandComponent() {
        this.brand_menu = new JPopupMenu();
        tableRowSelect(this.tbl_brand, brand_menu);
        this.brand_menu.add("Yeni").addActionListener(e -> {
            BrandView brandView = new BrandView(null);
            brandView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadBrandTable();
                    loadModelTable(null);
                    loadModelFilterBrand();
                }
            });
        });
        this.brand_menu.add("Güncelle").addActionListener(e -> {
            int selectBrandId = this.getTableSelectedRow(tbl_brand, 0);
            BrandView brandView = new BrandView(this.brandManager.getById(selectBrandId));
            brandView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadBrandTable();
                    loadModelTable(null);
                    loadModelFilterBrand();
                }
            });
        });
        this.brand_menu.add("Sil").addActionListener(e -> {
            if (Helper.confirm("sure")) {
                int selectBrandId = this.getTableSelectedRow(tbl_brand, 0);
                if (this.brandManager.delete(selectBrandId)) {
                    Helper.showMsg("done");
                    loadBrandTable();
                    loadModelTable(null);
                    loadModelFilterBrand();
                } else {
                    Helper.showMsg("error");
                }
            }
        });
    }

    private void createUIComponents() throws ParseException {
        this.fld_strt_date =new JFormattedTextField(new MaskFormatter("##/##/####"));
        this.fld_strt_date.setText("10/10/2023");
        this.fld_fnsh_date =new JFormattedTextField(new MaskFormatter("##/##/####"));
        this.fld_fnsh_date.setText("16/10/2023");
    }

    public void SystemExit(){
        btn_logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                LoginView loginView = new LoginView();
            }
        });
    }
}