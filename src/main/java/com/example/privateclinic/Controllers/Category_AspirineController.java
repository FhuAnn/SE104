package com.example.privateclinic.Controllers;

import com.example.privateclinic.DataAccessObject.*;
import com.example.privateclinic.ForeignKeyViolationException;
import com.example.privateclinic.Models.*;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import javax.swing.plaf.synth.Region;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Category_AspirineController implements Initializable {

    @FXML
    private JFXButton addButton;

    @FXML
    private JFXButton editButton;

    @FXML
    private JFXButton deleteButton;

    @FXML
    private TextField medicineNameTextField_1;

    @FXML
    private ComboBox<String> unitComboBox;

    @FXML
    private ComboBox<String> formComboBox;

    @FXML
    private ComboBox<String> useComboBox;

    @FXML
    private TextField medicineNameTextField_2, importTimesTextField, importQuantityTextField, importPriceTextField;

    @FXML
    private DatePicker importDatePicker;

    @FXML
    private JFXButton importButton;

    @FXML
    private TableView<Medicine> medicineTableView;

    @FXML
    private TableColumn<Medicine, Integer> idColumn;
    @FXML
    private TableColumn<Medicine, Integer> sttColumn;

    @FXML
    private TableColumn<Medicine, String> nameColumn;

    @FXML
    private TableColumn<Medicine, String> unitColumn;

    @FXML
    private TableColumn<Medicine, String> formColumn;

    @FXML
    private TableColumn<Medicine, String> useColumn;

    @FXML
    private TableColumn<Medicine, String> quantityColumn;

    @FXML
    private TableColumn<Medicine, Double> priceColumn;

    private MedicineDAO medicineDAO = new MedicineDAO();
    private final UnitDAO unitDAO = new UnitDAO();
    private final MedicineTypeDAO medicinetypeDAO = new MedicineTypeDAO();
    private final UseWayDAO usewayDAO = new UseWayDAO();
    private WarehouseDAO warehouseDAO = new WarehouseDAO();
    public Pane lbl_header;
    private final ObservableList<Medicine> medicineData = FXCollections.observableArrayList();
    private HistoryDAO historyDAO = new HistoryDAO();
    final private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private double xOffset = 0;
    private double yOffset =0;
    private User employee_init;
    public void init(User employee){
        employee_init = employee;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        unitComboBox.getItems().addAll("Viên", "Chai");
        formComboBox.getItems().addAll("Viên", "Lỏng", "Gel");
        useComboBox.getItems().addAll("Uống", "Bôi ngoài", "Xông", "Nhai");

        medicineDAO = new MedicineDAO();
        warehouseDAO = new WarehouseDAO();

        // Set up columns
        sttColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(medicineTableView.getItems().indexOf(cellData.getValue()) + 1).asObject());
        idColumn.setCellValueFactory(new PropertyValueFactory<>("maThuoc"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("tenThuoc"));
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("tenDonViTinh"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("giaBan"));
        formColumn.setCellValueFactory(new PropertyValueFactory<>("tenDangThuoc"));
        useColumn.setCellValueFactory(new PropertyValueFactory<>("tenCachDung"));

        medicineTableView.setStyle("-fx-selection-bar: #5A8F15;");
        // Load data into table
        loadMedicineData();
        setDatePickerToday();
        MedicineSelection();
        configureRowFactory();

        addButton.setOnAction(this::handleAddMedicine);
        editButton.setOnAction(this::handleEditMedicine);
        deleteButton.setOnAction(this::handleDeleteMedicine);
        importButton.setOnAction(this::handleImportButton);
        lbl_header.setOnMousePressed(mouseEvent -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });
        lbl_header.setOnMouseDragged(mouseEvent -> {
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.setX(mouseEvent.getScreenX()-xOffset);
            stage.setY(mouseEvent.getScreenY()-yOffset);
        });

    }

    private void loadMedicineData() {
        List<Medicine> medicines = medicineDAO.getAllMedicines();
        medicineData.addAll(medicines);
        medicineTableView.setItems(medicineData);
    }

    @FXML
    void handleAddMedicine(ActionEvent event) {

        String medicineName = medicineNameTextField_1.getText();

        if (medicineName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo!");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng nhập tên thuốc!");
            alert.showAndWait();
            return;
        }

        if (medicineDAO.isMedicineNameExists(medicineName, -1)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo!");
            alert.setHeaderText(null);
            alert.setContentText("Tên thuốc đã tồn tại!");
            alert.showAndWait();
        } else {
            String medicineUnit = unitComboBox.getValue();
            String medicineForm = formComboBox.getValue();
            String medicineUse = useComboBox.getValue();

            if (medicineUnit == null || medicineUnit.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo!");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng chọn đơn vị tính!");
                alert.showAndWait();
                return; // Dừng phương thức nếu đơn vị thuốc không hợp lệ
            }

            if (medicineForm == null || medicineForm.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo!");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng chọn dạng thuốc!");
                alert.showAndWait();
                return; // Dừng phương thức nếu dạng thuốc không hợp lệ
            }

            if (medicineUse == null || medicineUse.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo!");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng chọn cách dùng thuốc!");
                alert.showAndWait();
                return; // Dừng phương thức nếu cách dùng thuốc không hợp lệ
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận thêm");
            alert.setHeaderText("Bạn có chắc chắn muốn thêm?");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                int unitID = unitDAO.getUnitIDByName(medicineUnit);
                int formID = medicinetypeDAO.getMedicineTypeIDByName(medicineForm);
                int useID = usewayDAO.getUseWayIDByName(medicineUse);
                if(medicineDAO.addMedicine(new Medicine(medicineDAO.getNextMedicineId(), medicineName, unitID, 0, 0, formID, useID)));
                historyDAO.addHistory(new History(employee_init.getEmployee_id(),"Thêm DM thuốc ID :"+(medicineDAO.getNextMedicineId()-1)+" " ));
                medicineData.clear();
                clearMedicineFields();
                loadMedicineData();

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Thành công");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Đã thêm thuốc thành công.");
                successAlert.showAndWait();
            }
        }
    }

        @FXML
        void handleEditMedicine (ActionEvent event) {
            Medicine selectedMedicine = medicineTableView.getSelectionModel().getSelectedItem();
            String medicineName = medicineNameTextField_1.getText();
            String medicineUnit = unitComboBox.getValue();
            String medicineForm = formComboBox.getValue();
            String medicineUse = useComboBox.getValue();

            if (medicineName.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo!");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng nhập tên thuốc!");
                alert.showAndWait();
                return;
            }

            if (medicineDAO.isMedicineNameExists(medicineName, selectedMedicine.getMaThuoc())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo!");
                alert.setHeaderText(null);
                alert.setContentText("Tên thuốc đã tồn tại!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Xác nhận chỉnh sửa");
                alert.setHeaderText("Bạn có chắc chắn muốn chỉnh sửa?");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {

                    int unitID = unitDAO.getUnitIDByName(medicineUnit);
                    int formID = medicinetypeDAO.getMedicineTypeIDByName(medicineForm);
                    int useID = usewayDAO.getUseWayIDByName(medicineUse);

                    // Cập nhật thông tin của thuốc được chọn
                    selectedMedicine.setTenThuoc(medicineName);
                    selectedMedicine.setMaDonViTinh(unitID);
                    selectedMedicine.setMaDangThuoc(formID);
                    selectedMedicine.setMaCachDung(useID);

                    // Cập nhật thuốc trong cơ sở dữ liệu
                    if(medicineDAO.updateMedicine(selectedMedicine))
                        historyDAO.addHistory(new History(employee_init.getEmployee_id(),"Sửa DM thuốc ID:"+selectedMedicine.getMaThuoc()+"-"+selectedMedicine.getTenThuoc()+"" ));
                    medicineData.clear();
                    clearMedicineFields();
                    loadMedicineData();

                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Thành công");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Đã cập nhật thuốc thành công.");
                    successAlert.showAndWait();
                }
            }
        }

    @FXML
    void handleDeleteMedicine(ActionEvent event) {
        Medicine selectedMedicine = medicineTableView.getSelectionModel().getSelectedItem();
        if (selectedMedicine != null) {
            Alert confirmDeleteAlert = new Alert(Alert.AlertType.WARNING);
            confirmDeleteAlert.setTitle("Xác nhận xóa");
            confirmDeleteAlert.setHeaderText("Bạn có chắc chắn muốn xóa thuốc này?");
            confirmDeleteAlert.setContentText("Thuốc này sẽ bị xóa khỏi cơ sở dữ liệu");

            Optional<ButtonType> result = confirmDeleteAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                int medicineID = selectedMedicine.getMaThuoc();
                try {
                    if (warehouseDAO.deleteWarehouse(medicineID) && medicineDAO.deleteMedicine(medicineID)) {
                        historyDAO.addHistory(new History(employee_init.getEmployee_id(), "Xoá DM thuốc ID:" + medicineID + "-" + selectedMedicine.getTenThuoc() + ""));
                        medicineData.clear(); // Xóa dữ liệu hiện tại trong bảng
                        loadMedicineData(); // Tải lại dữ liệu từ cơ sở dữ liệu

                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Thành công");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("Đã xóa thuốc thành công.");
                        successAlert.showAndWait();
                    }
                } catch (ForeignKeyViolationException e) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Cảnh báo");
                    alert.setHeaderText(null);
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
        } else {
            // Hiển thị thông báo lỗi nếu không có hàng nào được chọn
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng chọn một thuốc để xóa.");
            alert.showAndWait();
        }
    }

    private void setDatePickerToday() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Đặt ngày hiện tại cho DatePicker
        importDatePicker.setValue(LocalDate.now());

        // Định dạng cách hiển thị của DatePicker
        importDatePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try {
                        return LocalDate.parse(string, dateFormatter);
                    } catch (DateTimeParseException e) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        });
    }

    @FXML
    void handleImportButton(ActionEvent event) {
        // Lấy thông tin từ các trường nhập liệu
        String medicineName = medicineNameTextField_2.getText();
        String importQuantityText = importQuantityTextField.getText();
        String importPriceText = importPriceTextField.getText();
        LocalDate selectedDate = importDatePicker.getValue();

        int importTimes = Integer.parseInt(importTimesTextField.getText());

        // Kiểm tra các trường nhập liệu có rỗng không
        if (medicineName.isEmpty() || importQuantityText.isEmpty() || importPriceText.isEmpty() || selectedDate == null) {
            // Hiển thị thông báo lỗi nếu các trường nhập liệu bị bỏ trống
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Lỗi");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Vui lòng điền đầy đủ thông tin.");
            errorAlert.showAndWait();
            return; // Dừng lại nếu có trường nhập liệu bị bỏ trống
        }

        // Chuyển đổi các giá trị nhập liệu sang kiểu số nếu có thể
        int importQuantity;
        double importPrice;

        try {
            importQuantity = Integer.parseInt(importQuantityText);
            importPrice = Double.parseDouble(importPriceText);
            if(importQuantity<=0) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Lỗi");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Số lượng nhập phải lớn hơn 0");
                errorAlert.showAndWait();
                importQuantityTextField.clear();
                return;
            }
        } catch (NumberFormatException e) {
            // Hiển thị thông báo lỗi nếu không thể chuyển đổi sang kiểu số
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Lỗi");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Nhập liệu không hợp lệ. Hãy nhập số vào các trường 'Số lượng', và 'Giá'.");
            errorAlert.showAndWait();
            return; // Dừng lại nếu có lỗi xảy ra
        }

        Alert confirmDeleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDeleteAlert.setTitle("Xác nhận nhập kho");
        confirmDeleteAlert.setHeaderText(null);
        confirmDeleteAlert.setContentText("Bạn chắc chắn muốn nhập kho?");

        Optional<ButtonType> result = confirmDeleteAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Kiểm tra xem tên thuốc có tồn tại không
            if (medicineDAO.isMedicineNameExists(medicineName, 0)) {
                // Lấy mã thuốc từ tên thuốc
                int medicineID = medicineDAO.getMedicineIDByName(medicineName);
                Medicine medicine = medicineDAO.getMedicineByName(medicineName);
                int currentQuantity = medicine.getSoLuong();
                int newQuantity = currentQuantity + importQuantity;
                medicine.setSoLuong(newQuantity);
                medicine.setGiaBan(importPrice);
                // Thêm hàng nhập vào cơ sở dữ liệu bằng cách gọi phương thức từ warehouseDAO
                if(warehouseDAO.addWarehouse(new Warehouse(warehouseDAO.getNextWarehouseId(), medicineID, 9, importTimes, importQuantity, selectedDate, importPrice))
                &&medicineDAO.updateMedicine(medicine))
                    historyDAO.addHistory(new History(employee_init.getEmployee_id(),"Nhập kho DM thuốc ID:"+medicineID+"-"+medicine.getTenThuoc()+"" ));
                medicineData.clear();
                loadMedicineData();
                // Hiển thị thông báo thành công
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Thành công");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Đã nhập kho thành công.");
                successAlert.showAndWait();

                // Xóa nội dung trong các trường nhập liệu
                clearImportFields();
            } else {
                // Hiển thị thông báo lỗi nếu tên thuốc không tồn tại
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Lỗi");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Tên thuốc không tồn tại trong cơ sở dữ liệu.");
                errorAlert.showAndWait();
            }
        }
    }

    private void MedicineSelection() {
        medicineTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Lấy thông tin của thuốc từ hàng được chọn
                Medicine selectedMedicine = medicineTableView.getSelectionModel().getSelectedItem();
                // Đặt thông tin của thuốc vào các trường TextField và ComboBox
                medicineNameTextField_1.setText(selectedMedicine.getTenThuoc());
                medicineNameTextField_2.setText(selectedMedicine.getTenThuoc());
                importTimesTextField.setText(String.valueOf(warehouseDAO.getMaxImportTimes(selectedMedicine.getMaThuoc()) + 1));
                unitComboBox.setValue(selectedMedicine.getTenDonViTinh());
                formComboBox.setValue(selectedMedicine.getTenDangThuoc());
                useComboBox.setValue(selectedMedicine.getTenCachDung());
            } else {
                // Nếu không có hàng nào được chọn, xóa nội dung của các trường
                medicineNameTextField_1.clear();
                medicineNameTextField_2.clear();
                importTimesTextField.clear();
                unitComboBox.getSelectionModel().clearSelection();
                formComboBox.getSelectionModel().clearSelection();
                useComboBox.getSelectionModel().clearSelection();
            }
        });
    }

    private void clearMedicineFields() {
        medicineNameTextField_1.clear();
        unitComboBox.getSelectionModel().clearSelection();
        formComboBox.getSelectionModel().clearSelection();
        useComboBox.getSelectionModel().clearSelection();
    }

    // Phương thức để xóa nội dung trong các trường nhập liệu sau khi nhập thành công
    private void clearImportFields() {
        medicineNameTextField_2.clear();
        importTimesTextField.clear();
        importQuantityTextField.clear();
        importPriceTextField.clear();
    }

    private void configureRowFactory() {
        medicineTableView.setRowFactory(tv -> {
            TableRow<Medicine> row = new TableRow<Medicine>() {
                @Override
                protected void updateItem(Medicine medicine, boolean empty) {
                    super.updateItem(medicine, empty);
                    if (medicine == null || empty) {
                        setStyle("");
                    } else {
                        int quantity = medicine.getSoLuong();
                        if (quantity < 50 && quantity > 0) {
                            setStyle("-fx-background-color: yellow;");
                        } else if (quantity == 0) {
                            setStyle("-fx-background-color: red;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            };

            return row;
        });
    }
    @FXML
    void close(MouseEvent event) {
        Stage s = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(s);
    }

    public void minimizeCategory(MouseEvent mouseEvent) {
        Model.getInstance().getViewFactory().minimizeStage((Stage) importTimesTextField.getScene().getWindow());
    }

}