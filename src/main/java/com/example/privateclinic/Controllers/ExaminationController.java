package com.example.privateclinic.Controllers;

import com.example.privateclinic.DataAccessObject.*;
import com.example.privateclinic.Models.*;
import com.example.privateclinic.WavPlayer;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public  class ExaminationController implements Initializable {
    public Pane btnClose;
    public DatePicker dp_date;
    public TableView<Medicine>  tbl_resultSearchMedicine;
    public TableView<Prescribe> tbl_chosenMedicine;
    public TableColumn<Prescribe,Integer> col_stt,
            col_ngay, col_sang, col_trua,col_chieu, col_toi,col_soLuongChosen;
    public TableColumn<Prescribe,String> col_tenthuocChosen,col_donViChosen,col_dangThuocChosen,
            col_cachDungChosen,col_maThuocChosen;
    public TableColumn<Prescribe,Double> col_donGiaChosen,col_thanhTienChosen;
    public TableColumn<Medicine,String> col_tenThuocResult,
            col_donViResult,col_dangThuocResult,
            col_cachDungResult;
    public TableColumn<Medicine,Integer> col_maThuocResult,col_soLuongResult;
    public TableColumn<Medicine,Double> col_giaBanResult;
    public Pane panel_MedicineResultSearch,panel_diseasesResultSearch;
    public Label lbl_searchMedicineString,lbl_searchDiseaseString;
    public Button btnKham;
    public Button btnThem;
    public Button btnXoa;
    public Button btnInBangKe;
    public Button btnLichSuKham;
    public AnchorPane pane_footer;
    public TitledPane tp_thongTin;
    public TitledPane tp_khamBenh;
    public TextField tf_trieuChung;
    public TextField tf_maBenhPhu;
    public TextField tf_tenBenhPhu;
    public TextField tf_luuY;
    public TitledPane tpkeThuoc;

    public HBox pane_optionPatient;

    public Label lbl_noMedicineResult;
    public Label lbl_noPatientResult;
    public Label lbl_noPickMedicine;
    public TableColumn<Disease,Integer> col_maBenh;
    public TableColumn<Disease,String> col_tenBenh;
    public TableView<Disease> tbl_resultSearchDisease;
    public Label lbl_noDiseaseResult;
    @FXML
    public TextField tf_maBenhChinh;
    @FXML
    public TextField tf_tenBenhChinh;
    public ToggleGroup tg_listCustomer;
    public TextField tf_searchIDName;
    public Button btnLuu;
    public Button btnCancel;
    public Button btnCallPatient;
    public Button btnInToaThuoc;
    @FXML
    RadioButton rad_patientWaiting,rad_patientDone;
    public Label  lbl_soLuong;
    @FXML
    Button btnLamMoi;
    @FXML
    RadioButton rad_men,rad_women, rad_womenVoice;
    @FXML
    TextField tf_mabn,tf_tenbn,tf_ngaysinh;
    @FXML
    TextField tf_ngay,tf_sang,tf_trua,tf_chieu,tf_toi;
    @FXML
    TextField tf_tenThuoc;
    @FXML
    TextField tf_donViTinh,tf_dangThuoc,tf_cachDung;
    @FXML
    public TableView<Patient> tbl_customer;
    @FXML
    public TableColumn <Patient,String> col_mabn;
    @FXML
    public TableColumn <Patient,String> col_tenbn;
    @FXML
    public TableColumn <Patient,String> col_sdt;

    ObservableList<Patient>  listWaitingPatients ;
    ObservableList<Patient> listDonePatients;
    ObservableList<Medicine>  listMedicines ;
    ObservableList<Disease> listDisiseases;
    MedicineDAO medicineDAO;

    PatientDAO patientDAO;
    DiseaseDAO diseaseDAO;
    ExaminationDAO examinationDAO;
    PrescribeDAO prescribeDAO;
    HistoryDAO historyDAO;
    Patient patientChosenBefore;
    Medicine medicineChosenBefore;
    Prescribe prescribeChosenBefore;
    ExaminationHistory examinationHistorySent;
    Disease disease_main,disease_sub;
    boolean isChanged;
    User user;
    public AnchorPane lbl_header,lbl_header2;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private double xOffset = 0;
    private double yOffset =0;
    public void initData(User _user)
    {
        this.user=_user;
    }
    public void initFromHistory(ExaminationHistory _examinationHistory) {
        examinationHistorySent=_examinationHistory;
        FillDataFromHistory();
        lbl_noPickMedicine.setVisible(false);
    }

    private void FillDataFromHistory() {
        Examination examination = examinationHistorySent.getExamination();
        ObservableList<Prescribe> prescribes = examinationHistorySent.getPrescribe();
        tf_trieuChung.setText(examination.getTrieuChung());
        tf_maBenhChinh.setText(String.valueOf(examination.getMaBenhChinh()));
        tf_tenBenhChinh.setText(examination.getTenBenhChinh());
        tf_maBenhPhu.setText(String.valueOf(examination.getMaBenhPhu()));
        tf_tenBenhPhu.setText(examination.getTenBenhPhu());
        tf_luuY.setText(examination.getLuuy());
        tbl_chosenMedicine.setItems(prescribes);
        panel_diseasesResultSearch.setVisible(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Initialization
        SetUp();
        //set up
        setUpTableView();
        setFindDiseasesByIDorName_mainDisease(tf_maBenhChinh,tf_tenBenhChinh);
        setFindDiseasesByIDorName_subDisease(tf_maBenhPhu,tf_tenBenhPhu);
        //load
        LoadListPatients(Date.valueOf(LocalDate.now()));
        showDataPatients_waiting();// tải danh sách customers
        //addListener
        addListenerTextChanged(tf_ngay,tf_sang,tf_sang,tf_trua,tf_chieu,tf_toi);
        tg_listCustomer.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle oldValue, Toggle newValue) {
                RadioButton rad_selected = (RadioButton)newValue;
                if(rad_selected.getId().equals("rad_patientWaiting")) showDataPatients_waiting();// tải danh sách benh nhan cho
                else showDataPatients_done();// tải danh sách benh nhan da kham
                patientChosenBefore=null;
            }
        });
        btnLamMoi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                LoadListPatients(Date.valueOf(dp_date.getValue()));
                tbl_customer.setItems(listWaitingPatients);
                tbl_customer.getSelectionModel().clearSelection();
                rad_patientWaiting.setSelected(true);
                showDataPatients_waiting();
            }
        });
        tbl_customer.setOnMouseClicked(mouseEvent -> {
            pane_optionPatient.setDisable(false);
            if(mouseEvent.getClickCount()==2)
            {
                Patient patient = tbl_customer.getSelectionModel().getSelectedItem();
                if(!String.valueOf(patient.getPatientId()).equals(tf_mabn.getText()) &&!tf_mabn.getText().isEmpty()) {
                    if ( ShowYesNoAlert("chuyển sang "+patient.getPatientName()) == JOptionPane.YES_OPTION) {
                        /* existFilled();*/
                        SetDisable();
                        ResetAllTextField();
                        tbl_chosenMedicine.getItems().clear();
                        ResetTF(tf_maBenhChinh, tf_tenBenhChinh, tf_tenBenhPhu, tf_tenBenhPhu);
                        fillDataPatient_exam();
                        lbl_noPatientResult.setVisible(false);
                    }
                } else {
                        SetDisable();
                        ResetAllTextField();
                        tbl_chosenMedicine.getItems().clear();
                        ResetTF(tf_maBenhChinh, tf_tenBenhChinh, tf_tenBenhPhu, tf_tenBenhPhu);
                        fillDataPatient_exam();
                        lbl_noPatientResult.setVisible(false);
                }
            }
        });
        tf_tenThuoc.textProperty().addListener((observable,oldValue,newValue) -> {
            //show
            //hiển thi ket qua tim kiem
            if(!newValue.trim().equals(""))
            {
                showResultMedicineList(tf_tenThuoc.getText());
                if(panel_diseasesResultSearch.isVisible()) panel_diseasesResultSearch.setVisible(false); // hide disease to open medicine
                panel_MedicineResultSearch.setVisible(true);
                medicineChosenBefore=null;
                ResetTF_KeThuoc();
                btnThem.setText("Thêm");
            }
            else panel_MedicineResultSearch.setVisible(false);
        });
        tbl_resultSearchMedicine.setRowFactory(medicineTableView -> new TableRow<>() {
            @Override
            protected void updateItem(Medicine medicine, boolean empty) {
                super.updateItem(medicine, empty);
                getStyleClass().removeAll("table-row-cell-red", "table-row-cell-yellow");
                if (medicine != null && !empty) {
                    if (medicine.getSoLuong() == 0) {
                        getStyleClass().add("table-row-cell-red");
                    } else if (medicine.getSoLuong() < 30) {
                        getStyleClass().add("table-row-cell-yellow");
                    }
                }
            }
        });
        tf_searchIDName.textProperty().addListener((observable, oldValue,newValue) ->
        {
            if(!newValue.trim().isEmpty())
            {
                if(rad_patientWaiting.isSelected()) {
                    tbl_customer.setItems(listWaitingPatients);
                    tbl_customer.getSelectionModel().clearSelection();
                }
                else tbl_customer.setItems(listDonePatients); // cap nhat lai danh sach goc vao tbl
                SearchPatientResultList(tf_searchIDName.getText());
            } else {
                tbl_customer.setItems(listWaitingPatients);
                tbl_customer.getSelectionModel().clearSelection();
                if(!listWaitingPatients.isEmpty()) lbl_noPatientResult.setVisible(false);
                rad_patientWaiting.setSelected(true);
            }
        });
        dp_date.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SetDisable();
                tbl_chosenMedicine.getItems().clear();
                LoadListPatients(Date.valueOf(dp_date.getValue()));
                tbl_customer.setItems(listWaitingPatients);
                tbl_customer.getSelectionModel().clearSelection();
                rad_patientWaiting.setSelected(true);
                showDataPatients_waiting();
                ResetAllTextField();
                patientChosenBefore=null;
                pane_optionPatient.setDisable(true);
            }
        });
        tbl_resultSearchMedicine.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount()==2)
                {
                    Medicine medicine = tbl_resultSearchMedicine.getSelectionModel().getSelectedItem();
                    if(!isProductExists(tbl_chosenMedicine.getItems(),medicine))
                    {
                        tf_tenThuoc.setText(medicine.getTenThuoc());
                        tf_cachDung.setText(medicine.getTenCachDung());
                        tf_dangThuoc.setText(medicine.getTenDangThuoc());
                        tf_donViTinh.setText(medicine.getTenDonViTinh());
                        medicineChosenBefore=medicine;
                        btnThem.setDisable(false);
                        panel_MedicineResultSearch.setVisible(false);
                        SetDisableKeThuoc(false);
                    }
                    else {
                        showAlert("Warning","Thuốc mà bạn chọn đã có trong danh sách!");
                        medicineChosenBefore=null;
                    }
                }
            }
        });
        tbl_chosenMedicine.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btnXoa.setDisable(false);
                if(mouseEvent.getClickCount()==2)
                {
                    prescribeChosenBefore = tbl_chosenMedicine.getSelectionModel().getSelectedItem();
                    if(prescribeChosenBefore!=null)
                    {
                        tf_ngay.setDisable(false);
                        tf_sang.setDisable(false);
                        tf_trua.setDisable(false);
                        tf_chieu.setDisable(false);
                        tf_toi.setDisable(false);
                        FillToPanel_KeThuoc(prescribeChosenBefore);
                        panel_MedicineResultSearch.setVisible(false);
                        btnThem.setText("Lưu");
                    }
                }
            }
        });
        tbl_resultSearchDisease.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount()==2)
                {
                    Disease disease = tbl_resultSearchDisease.getSelectionModel().getSelectedItem();
                    if(disease!=null)
                    {
                        if(panel_diseasesResultSearch.getLayoutY()==191) // benh chinh
                        {
                            if(tf_maBenhPhu.getText().isEmpty()||disease.getMaBenh()!=Integer.parseInt(tf_maBenhPhu.getText())) {
                                tf_maBenhChinh.setText(String.valueOf(disease.getMaBenh()));
                                tf_tenBenhChinh.setText(String.valueOf(disease.getTenBenh()));
                                panel_diseasesResultSearch.setVisible(false);
                                disease_main=disease;
                            } else {
                                showAlert("Warning","Không được trùng với bệnh phụ!");
                            }

                        }
                        else { // benh phu
                            if(tf_maBenhChinh.getText().isEmpty()|| disease.getMaBenh()!=Integer.parseInt(tf_maBenhChinh.getText())) {
                                tf_maBenhPhu.setText(String.valueOf(disease.getMaBenh()));
                                tf_tenBenhPhu.setText(String.valueOf(disease.getTenBenh()));
                                panel_diseasesResultSearch.setVisible(false);
                                disease_sub=disease;
                            }else {
                                showAlert("Warning","Không được trùng với bệnh chính!");
                            }
                        }
                    }
                }
            }
        });
        btnKham.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(rad_patientWaiting.isSelected()) {
                    if(!tbl_customer.getSelectionModel().isEmpty())
                    {
                        SetUnDisable();
                        tbl_customer.getSelectionModel().clearSelection();
                    }
                }
            }
        });
        btnThem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(btnThem.getText().equals("Thêm"))
                {
                    if(checkFillMedicine())
                    {
                        addMedicineToMedicineTable(medicineChosenBefore.getMaThuoc(), medicineChosenBefore.getGiaBan(),-1);
                        //updateIndexColumn(tbl_chosenMedicine);
                        lbl_noPickMedicine.setVisible(false);
                        SetDisableKeThuoc(true);
                        ResetTF_KeThuoc();
                        tf_tenThuoc.clear();
                        medicineChosenBefore=null;
                    }
                } else { //Luu
                    if(checkFillMedicine())
                    {
                        int pos_delete= DeleteRowInChosenTable(tbl_chosenMedicine.getItems(),tbl_chosenMedicine.getSelectionModel().getSelectedItem(),false);
                        addMedicineToMedicineTable(prescribeChosenBefore.getMaThuoc(),prescribeChosenBefore.getDonGia(),pos_delete);
                        btnThem.setText("Thêm");
                    }
                }
            }
        });
        btnXoa.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Prescribe prescribe = tbl_chosenMedicine.getSelectionModel().getSelectedItem();
                if(!tbl_chosenMedicine.getItems().isEmpty()||prescribe==null)
                {
                    int response = ShowYesNoAlert("delete");
                    if (response == JOptionPane.YES_OPTION) {
                        DeleteRowInChosenTable(tbl_chosenMedicine.getItems(),prescribe,true);
                        if(tbl_chosenMedicine.getItems().isEmpty())
                            lbl_noPickMedicine.setVisible(true);
                    }
                    else
                    {
                    }
                }
            }
        });
        btnLichSuKham.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(patientChosenBefore!=null) Model.getInstance().getViewFactory().showHistoryExamination(patientChosenBefore,ExaminationController.this);
            }
        });
        btnLuu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(tp_thongTin.isVisible()&& isFullFilled())
                {
                    if(ShowYesNoAlert("lưu và kết thúc khám bệnh") == JOptionPane.YES_OPTION)
                    {
                        if(saveExaminationInformationToDabase())  {
                            showAlert("Notification","Lưu dữ liệu khám bệnh thành công!");
                            SetDisable();
                            LoadListPatients(Date.valueOf(dp_date.getValue()));
                            tbl_customer.setItems(listWaitingPatients);
                            tbl_customer.getSelectionModel().clearSelection();
                            lbl_noPatientResult.setVisible(false);
                        }
                    }
                    else {}
                }
            }
        });
        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                {
                    if(!IsBlank()) {
                        int request = ShowYesNoAlert("huỷ khám");
                        if(request == JOptionPane.YES_OPTION){
                            ResetAllTextField();
                            SetDisable();
                            tbl_chosenMedicine.getItems().clear();
                            lbl_noPatientResult.setVisible(false);
                        }
                    } else {
                        ResetAllTextField();
                        SetDisable();
                        tbl_chosenMedicine.getItems().clear();
                        lbl_noPatientResult.setVisible(false);
                    }
                    examinationHistorySent=null;
                }
            }
        });
        btnCallPatient.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(rad_patientWaiting.isSelected()) {
                    Patient patient = tbl_customer.getSelectionModel().getSelectedItem();
                    if(patient!=null) {
                        String gender;
                        int id;
                        if(rad_womenVoice.isSelected()) gender = "women";
                        else gender ="men";
                        id =patient.getPatientId();
                        WavPlayer.playSound(gender+id + ".wav");
                    }
                }
            }
        });
        btnInToaThuoc.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    if(IsBlank()) {
                        if(ShowYesNoAlert("in toa thuốc")==JOptionPane.YES_OPTION) printToaThuoc();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        btnInBangKe.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(IsBlank()) {
                    if(patientChosenBefore!=null||examinationHistorySent!=null) {
                        try {
                            if(ShowYesNoAlert("xuất bảng kê")==JOptionPane.YES_OPTION)  printBangke();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });

    }
    private void SetUp() {
        patientDAO = new PatientDAO();
        medicineDAO= new MedicineDAO();
        diseaseDAO = new DiseaseDAO();
        examinationDAO= new ExaminationDAO();
        prescribeDAO = new PrescribeDAO();
        historyDAO = new HistoryDAO();
        dp_date.setValue(LocalDate.now());
        lbl_soLuong.setText("0");
        pane_optionPatient.setDisable(true);
        rad_patientWaiting.setSelected(true);
        isChanged=false;
        SetDisable();
        tbl_chosenMedicine.getItems().clear();
        lbl_header.setOnMousePressed(mouseEvent -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });

        lbl_header.setOnMouseDragged(mouseEvent -> {
            Stage stage = (Stage) lbl_header.getScene().getWindow();
            stage.setX(mouseEvent.getScreenX()-xOffset);
            stage.setY(mouseEvent.getScreenY()-yOffset);
        });
        lbl_header2.setOnMousePressed(mouseEvent -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });

        lbl_header2.setOnMouseDragged(mouseEvent -> {
            Stage stage = (Stage) lbl_header.getScene().getWindow();
            stage.setX(mouseEvent.getScreenX()-xOffset);
            stage.setY(mouseEvent.getScreenY()-yOffset);
        });
    }

    private void SetDisableKeThuoc(boolean bool) {
        tf_sang.setDisable(bool);
        tf_trua.setDisable(bool);
        tf_chieu.setDisable(bool);
        tf_toi.setDisable(bool);
        tf_ngay.setDisable(bool);
    }

    private boolean IsBlank() { // tồn tại một tf đã điền thì trả về false
        return !tbl_chosenMedicine.getItems().isEmpty() && !tf_trieuChung.getText().isEmpty() && !tf_luuY.getText().isEmpty() && !tf_maBenhPhu.getText().isEmpty() && !tf_maBenhChinh.getText().isEmpty();
    }


    private boolean saveExaminationInformationToDabase() {
        int examId =saveExamination();// lay ma kham benh
        if(savePrescibes(examId)&&examId>0)
        {
            return saveHistory(examId);
        }
        return false;
    }

    private boolean saveHistory(int _id) {
        History history = new History(user.getEmployee_id(), STR."Khám bệnh ID: \{_id}");
        return historyDAO.addHistory(history);
    }

    private int saveExamination() {
        Examination examination = new Examination(user.getEmployee_id(), Integer.parseInt(tf_mabn.getText()),
                Date.valueOf(LocalDate.now()), Integer.parseInt(tf_maBenhChinh.getText()), Integer.parseInt(tf_maBenhPhu.getText()),
                tf_trieuChung.getText(), tf_luuY.getText());
        int examId = examinationDAO.addExamination(examination);
        if(examId>=-1) System.out.println("saved Examination!");
        return examId;
    }

    private boolean savePrescibes(int id) {
        ObservableList<Prescribe> listChosenPrescribe = tbl_chosenMedicine.getItems();
        for(Prescribe prescribe: listChosenPrescribe)
        {
            if(prescribeDAO.addPrescribe(id,prescribe)) {
                System.out.println("saved Prescibes;");
                if (!medicineDAO.UpdateMedicineAfterExam(prescribe.getMaThuoc(),prescribe.getSoLuong())) return false;
            } else return false;
        }
        System.out.println("savePrescides");
        return true;
    }

    private boolean isFullFilled() {
        if (tf_mabn.getId().isEmpty()) {
            return false;
        }
        if(tf_trieuChung.getText().isEmpty()||tf_maBenhChinh.getText().isEmpty()
                ||tf_luuY.getText().isEmpty()) {
            showAlert("Warning","Thiếu thông tin khám bệnh!");
            return false;
        }
        if(tbl_chosenMedicine.getItems().isEmpty()) {
            showAlert("Warning","Chưa kê thuốc cho bệnh nhân");
            return false;
        }
        return true;
    }

    private void SearchPatientResultList(String searchString) {
        String lowerCase = normalizeString(searchString.toLowerCase());
        ObservableList<Patient> patients = tbl_customer.getItems();
        ObservableList<Patient> listResult = FXCollections.observableArrayList(
                patients.stream()
                        .filter(customer ->
                                normalizeString(String.valueOf(customer.getPatientId()).toLowerCase()).startsWith(lowerCase) ||
                                        normalizeString(customer.getPatientName().toLowerCase()).contains(lowerCase))
                        .collect(Collectors.toList())
        );
        tbl_customer.setItems(listResult);
        if (!listResult.isEmpty()) {
            lbl_noPatientResult.setVisible(false);
        } else {
            lbl_noPatientResult.setVisible(true);
        }
    }

    private static String normalizeString(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
    public void setFindDiseasesByIDorName_mainDisease(TextField...textFields)
    {
        for(TextField tf : textFields)
        {
            tf.textProperty().addListener((observable,oldValue,newValue) ->
            {
                disease_main = null;
                if(tf.getId().equals("tf_maBenhChinh"))
                {
                    if (!newValue.matches("\\d*")) {
                        tf.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                } else {
                    if (!newValue.matches("[^\\d]*")) {
                        tf.setText(newValue.replaceAll("\\d", ""));
                    }
                }

                if(!newValue.isEmpty()&&!tp_khamBenh.isDisable()&&disease_main==null)
                {
                    showResultDiseasesSearch(tf.getText());
                    if(panel_MedicineResultSearch.isVisible()) panel_MedicineResultSearch.setVisible(false);
                    panel_diseasesResultSearch.setVisible(true);
                    panel_diseasesResultSearch.setLayoutX(1000);
                    panel_diseasesResultSearch.setLayoutY(191);
                }
                else panel_diseasesResultSearch.setVisible(false);
            });
        }
    }
    public void setFindDiseasesByIDorName_subDisease(TextField...textFields)
    {
        for(TextField tf : textFields)
        {
            tf.textProperty().addListener((observable,oldValue,newValue) ->
            {
                disease_sub=null;
                if(tf.getId().equals("tf_maBenhPhu"))
                {
                    if (!newValue.matches("\\d*")) {
                        tf.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                } else {
                    if (!newValue.matches("[^\\d]*")) {
                        tf.setText(newValue.replaceAll("\\d", ""));
                    }
                }
                if(!newValue.trim().isEmpty()&&!tp_khamBenh.isDisable()&&disease_sub==null)
                {
                    showResultDiseasesSearch(tf.getText());
                    if(panel_MedicineResultSearch.isVisible()) panel_MedicineResultSearch.setVisible(false);
                    panel_diseasesResultSearch.setVisible(true);
                    panel_diseasesResultSearch.setLayoutX(1000);
                    panel_diseasesResultSearch.setLayoutY(234);
                }
                else panel_diseasesResultSearch.setVisible(false);
            });
        }
    }
    private void showResultDiseasesSearch(String string) {;
        listDisiseases = diseaseDAO.searchDiseaseByIDorName(string);
        tbl_resultSearchDisease.setItems(listDisiseases);
        tbl_resultSearchDisease.getSelectionModel().clearSelection();
        lbl_searchDiseaseString.setText(string);
        if(!tbl_resultSearchDisease.getItems().isEmpty()){
            lbl_noDiseaseResult.setVisible(false);
        }
        else {
            lbl_noDiseaseResult.setVisible(true);
        }
    }
    private int DeleteRowInChosenTable(ObservableList<Prescribe> prescribes,Prescribe deletePrescribe,boolean updateIndex) {
        int index =0,pos_delete=0;
        boolean flag =false;
        for(Prescribe prescribe : prescribes)
        {
            if(prescribe.getMaThuoc()==deletePrescribe.getMaThuoc()) {
                pos_delete=index;
                flag=true;
            }
            if(flag && index+1<=prescribes.size()) {
                if(updateIndex) // giải quyết vd co phai cap nhat index khong
                prescribes.get(index).setSothuTu(index);
                else break;
            }
            index++;
        }
        prescribes.remove(pos_delete);
        return pos_delete;
    }

    private void FillToPanel_KeThuoc(Prescribe prescribe) {
        tf_tenThuoc.setText(prescribe.getTenThuoc());
        tf_donViTinh.setText(prescribe.getTenDonViTinh());
        tf_cachDung.setText(prescribe.getTenCachDung());
        tf_dangThuoc.setText(prescribe.getTenDangThuoc());
        tf_ngay.setText(String.valueOf(prescribe.getNgay()));
        tf_sang.setText(String.valueOf(prescribe.getSang()));
        tf_trua.setText(String.valueOf(prescribe.getTrua()));
        tf_chieu.setText(String.valueOf(prescribe.getChieu()));
        tf_toi.setText(String.valueOf(prescribe.getToi()));
    }

    private boolean isProductExists(ObservableList<Prescribe> prescribes, Medicine medicine) {
        for (Prescribe prescribe : prescribes) {
            // So sánh sản phẩm da chon với từng sản phẩm trong danh sách
            if (prescribe.getMaThuoc()==medicine.getMaThuoc()) {
                return true; // Nếu sản phẩm đã tồn tại, trả về true
            }
        }
        return false;
    }

    private void setUpTableView() {
        col_stt.setCellValueFactory(new PropertyValueFactory<>("sothuTu"));
        col_maThuocChosen.setCellValueFactory(new PropertyValueFactory<>("maThuoc"));
        col_tenthuocChosen.setCellValueFactory(new PropertyValueFactory<>("tenThuoc"));
        col_donViChosen.setCellValueFactory(new PropertyValueFactory<>("tenDonViTinh"));
        col_dangThuocChosen.setCellValueFactory(new PropertyValueFactory<>("tenDangThuoc"));
        col_cachDungChosen.setCellValueFactory(new PropertyValueFactory<>("tenCachDung"));
        col_ngay.setCellValueFactory(new PropertyValueFactory<>("ngay"));
        col_sang.setCellValueFactory(new PropertyValueFactory<>("sang"));
        col_trua.setCellValueFactory(new PropertyValueFactory<>("trua"));
        col_chieu.setCellValueFactory(new PropertyValueFactory<>("chieu"));
        col_toi.setCellValueFactory(new PropertyValueFactory<>("toi"));
        col_soLuongChosen.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        col_donGiaChosen.setCellValueFactory(new PropertyValueFactory<>("donGia"));
        col_thanhTienChosen.setCellValueFactory(new PropertyValueFactory<>("thanhTien"));

        //bang tim kiem thuoc
        col_maThuocResult.setCellValueFactory(new PropertyValueFactory<>("maThuoc"));
        col_tenThuocResult.setCellValueFactory(new PropertyValueFactory<>("tenThuoc"));
        col_donViResult.setCellValueFactory(new PropertyValueFactory<>("tenDonViTinh"));
        col_dangThuocResult.setCellValueFactory(new PropertyValueFactory<>("tenDangThuoc"));
        col_cachDungResult.setCellValueFactory(new PropertyValueFactory<>("tenCachDung"));
        col_soLuongResult.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        col_giaBanResult.setCellValueFactory(new PropertyValueFactory<>("giaBan"));

        //bang benh nhan
        col_mabn.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        col_tenbn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        col_sdt.setCellValueFactory(new PropertyValueFactory<>("patientPhoneNumber"));

        //bang ten benh
        col_maBenh.setCellValueFactory(new PropertyValueFactory<>("maBenh"));
        col_tenBenh.setCellValueFactory(new PropertyValueFactory<>("tenBenh"));
    }

    private void updateIndexColumn(TableView<Prescribe> tableView) {
            ObservableList<Prescribe> prescribes = tableView.getItems();
            for (int i = 0; i < prescribes.size(); i++) {
                prescribes.get(i).setSothuTu(i + 1);
            }
    }

    private boolean checkFillMedicine() {
        if(Integer.parseInt(lbl_soLuong.getText()) == 0)
        {
            showAlert("Warning","Chưa điền liều lượng");
            return false;
        }
        if(medicineChosenBefore==null&&btnThem.getText().equals("Thêm"))
        {
            showAlert("Warning","Chưa chọn một loại thuốc nào!");
            return false;
        }
        if(prescribeChosenBefore==null&&btnThem.getText().equals("Lưu"))
        {
            return false;
        }
        if(medicineChosenBefore.getSoLuong()<= Integer.parseInt(lbl_soLuong.getText())) {
            showAlert("Warning","Số lượng "+medicineChosenBefore.getTenThuoc()+" trong kho là "+medicineChosenBefore.getSoLuong()+", hãy nhập thêm rồi quay lại!");
            ResetTF_KeThuoc();
            return false;
        }
        return true;
    }

    private void ResetAllTextField() {
        ResetTF(tf_cachDung,tf_dangThuoc,tf_donViTinh,
                tf_tenThuoc);
        ResetTFExam();
        ResetTF_KeThuoc();
    }

    private void ResetTFExam() {
        ResetTF(tf_tenBenhChinh,tf_maBenhChinh,tf_luuY,tf_maBenhPhu,tf_tenBenhPhu,tf_trieuChung);
    }

    private void ResetTF_KeThuoc() {
        tf_sang.setText("0");
        tf_trua.setText("0");
        tf_chieu.setText("0");
        tf_toi.setText("0");
        tf_ngay.setText("1");
        lbl_soLuong.setText("0");
    }

    private void ResetTF(TextField...tfs) {
        for(TextField tf : tfs)
        {
            tf.clear();
        }
    }

    private void addMedicineToMedicineTable(int maThuoc,double donGia,int position) {
        Prescribe prescribe = new Prescribe(maThuoc,tf_tenThuoc.getText(),tf_donViTinh.getText(),tf_dangThuoc.getText(),
                tf_cachDung.getText(),donGia,
                Integer.parseInt(tf_ngay.getText()),Integer.parseInt(tf_sang.getText()),
                Integer.parseInt(tf_trua.getText()), Integer.parseInt(tf_chieu.getText()),
                Integer.parseInt(tf_toi.getText()), Integer.parseInt(lbl_soLuong.getText()));
        if(position==-1) // thêm vào cuoi nhu binh thuong
        {
            int stt = tbl_chosenMedicine.getItems().size();
            prescribe.setSothuTu(stt+1); // thu tu la 1, vi tri la 0
            tbl_chosenMedicine.getItems().add(stt,prescribe);
            tbl_chosenMedicine.getSelectionModel().clearSelection();
        }
        else {
            prescribe.setSothuTu(position+1);
            tbl_chosenMedicine.getItems().add(position,prescribe);// them vao vi tri
            tbl_chosenMedicine.getSelectionModel().clearSelection();
        }
    }

    private void SetUnDisable() {
        pane_optionPatient.setDisable(false);
        tpkeThuoc.setDisable(false);
        tp_khamBenh.setDisable(false);
        tp_thongTin.setDisable(false);
    }

    private void SetDisable() {
        tpkeThuoc.setDisable(true);
        tp_khamBenh.setDisable(true);
        tp_thongTin.setDisable(true);
        lbl_noMedicineResult.setVisible(true);
        lbl_noPatientResult.setVisible(true);
        lbl_noPickMedicine.setVisible(true);
        panel_MedicineResultSearch.setVisible(false);
        panel_diseasesResultSearch.setVisible(false);
    }

    private void LoadListPatients(Date date) {
        listWaitingPatients=patientDAO.getPatientsFromReceptionByDate(Date.valueOf(dp_date.getValue()));
        listDonePatients=patientDAO.getPatientsDoneByDate(date);
    }

    private void showResultMedicineList(String search) {
        listMedicines= medicineDAO.searchMedicineByIDorName(search);
        tbl_resultSearchMedicine.setItems(listMedicines);
        tbl_resultSearchMedicine.getSelectionModel().clearSelection();
        lbl_searchMedicineString.setText(search);
        if(!tbl_resultSearchMedicine.getItems().isEmpty())
        {
            lbl_noMedicineResult.setVisible(false);
            panel_diseasesResultSearch.setVisible(false); // hide disease to open medicine
            panel_MedicineResultSearch.setVisible(true);
        }
        else
        {
            lbl_noMedicineResult.setVisible(true);
        }
    }

    private void addListenerTextChanged(TextField...tfs) {
        for(TextField textField : tfs)
        {
            textField.textProperty().addListener((observable,oldValue,newValue)->{
                if (!newValue.matches("\\d*")) {
                    textField.setText(newValue.replaceAll("[^\\d]", ""));
                    if(textField.getText().isEmpty()){
                        textField.setText(newValue.replaceAll("[^\\d]", "0"));
                    }
                }
                UpTotal();
            });
        }
    }
    private void UpTotal() {
       int total =0;
       int ngay = tf_ngay.getText().isBlank()? 1:Integer.parseInt(tf_ngay.getText().trim());
       int sang= tf_sang.getText().isBlank() ? 0 : Integer.parseInt(tf_sang.getText().trim());
       int trua= tf_trua.getText().isBlank() ? 0 : Integer.parseInt(tf_trua.getText().trim());
       int chieu= tf_chieu.getText().isBlank() ? 0 : Integer.parseInt(tf_chieu.getText().trim());
       int toi= tf_toi.getText().isBlank() ? 0 : Integer.parseInt(tf_toi.getText().trim());
       total= (sang+trua+chieu+toi)*ngay;
       lbl_soLuong.setText(String.valueOf(total));
    }

    private void showDataPatients_waiting() {
        tbl_customer.setItems(listWaitingPatients);
        tbl_customer.getSelectionModel().clearSelection();
        //Kiem tra danh sách cho co null khong
        if(listWaitingPatients.isEmpty()) {
            showAlert("Warning","Danh sách bệnh nhân chờ vào ngày " +dp_date.getValue() +" trống!");
            lbl_noPatientResult.setVisible(true);
        }
        else lbl_noPatientResult.setVisible(false);
    }
    private void showDataPatients_done() {
        tbl_customer.setItems(listDonePatients);
        tbl_customer.getSelectionModel().clearSelection();
        //Kiem tra danh sách cho co null khong
        if(listDonePatients.isEmpty()) {
            showAlert("Warning","Danh sách đã khám vào ngày "+dp_date.getValue()+ " trống!");
            lbl_noPatientResult.setVisible(true);
        }
        else lbl_noPatientResult.setVisible(false);
    }
    public void fillDataPatient_exam()
    {
        patientChosenBefore = tbl_customer.getSelectionModel().getSelectedItem();
        tf_mabn.setText(String.valueOf(patientChosenBefore.getPatientId()));
        tf_ngaysinh.setText(String.valueOf(patientChosenBefore.getPatientBirth()));
        tf_tenbn.setText(patientChosenBefore.getPatientName());
        if(patientChosenBefore.getPatientGender().equals("Nam"))
            rad_men.setSelected(true);
        else rad_women.setSelected(true);
    }
    public void close(MouseEvent mouseEvent) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
    }
    private void showAlert(String tilte,String string) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(tilte);
        alert.setHeaderText(null);
        alert.setContentText(string);
        alert.showAndWait();
    }
    private int ShowYesNoAlert(String string) {
        JFrame frame = new JFrame("Table Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        return JOptionPane.showConfirmDialog(frame, "Có phải bạn muốn "+string+"?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }
    private void printToaThuoc() throws IOException {
        Document document = new Document();

        String path = STR."\\Prescription\\\{removeAccentsAndSpaces(tf_tenbn.getText())}_toa.pdf";
        try {
            String maTenBenhPhu="";
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            String fontPath = "notosans-regular.ttf";
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            Font boldFont = new Font(baseFont, 13, Font.BOLD);
            Font titleBoldFont = new Font(baseFont, 17, Font.BOLD);
            Font regularFont = new Font(baseFont, 14);
            Font footerbold = new Font(baseFont, 13,Font.BOLD);

            document.add(new Paragraph("PHÒNG MẠCH TƯ", boldFont));
            document.add(new Paragraph("UIT CLINIC", boldFont));
            document.add(new Paragraph("Email: greenuitk17@gmail.com", footerbold));
            document.add(new Paragraph("Số điện thoại: 1900 1555", footerbold));
            document.add(new Paragraph("Địa chỉ:  136, Linh Trung, Thủ Đức, TP Thủ Đức", regularFont));
            document.add(new Paragraph("                                                ĐƠN THUỐC", titleBoldFont));
            String gioitinh ;
            if(rad_men.isSelected()) gioitinh = "Name";
            else {
                gioitinh = "Nữ";
            }
            document.add(new Paragraph(STR."Mã BN:  \{tf_mabn.getText()} - Họ tên:  \{tf_tenbn.getText()} - Ngày sinh:  \{tf_ngaysinh.getText()} - Giới tính:  \{(gioitinh)}",regularFont));
            document.add(new Paragraph(STR."Triệu chứng:  \{tf_trieuChung.getText()}", regularFont));
            if(!tf_maBenhPhu.getText().isEmpty()) maTenBenhPhu = STR."; (\{tf_maBenhPhu.getText()}) \{tf_maBenhChinh.getText()} ";
            document.add(new Paragraph(STR."Chẩn đoán:     \{tf_maBenhPhu.getText()}  -  \{tf_tenBenhChinh.getText()}"+maTenBenhPhu, regularFont));
            document.add(new Paragraph("\n                                              THUỐC ĐIỀU TRỊ", titleBoldFont));
            int index=0;
            for(Prescribe prescribe:tbl_chosenMedicine.getItems()){
                index++;
                document.add(new Paragraph(prescribe.getSothuTu() + ") " + STR."\{prescribe.getTenThuoc()}                                                                                         SL: " +prescribe.getSoLuong() , boldFont));
                String sang="",trua="",chieu="",toi="";
                if(prescribe.getSang()>0) sang =STR."Sáng: \{prescribe.getSang()} viên";
                if(prescribe.getTrua()>0) trua =STR."Trưa: \{prescribe.getTrua()} viên";
                if(prescribe.getChieu()>0) chieu =STR."Chiều: \{prescribe.getChieu()} viên";
                if(prescribe.getToi()>0) toi =STR."Tối: \{prescribe.getToi()} viên";
                document.add(new Paragraph("        Uống:    "+sang+"          "+trua+"          "+chieu+"             "+toi+"" , regularFont));
            }
            LocalDate date = LocalDate.now();
            document.add(new Paragraph(STR."\nLời dặn: \{tf_luuY.getText()}                                                              Ngày "+date.getDayOfMonth()+ " tháng " + date.getMonthValue() + " năm " + date.getYear(), boldFont));
            document.add(new Paragraph(STR."Cộng khoản:     " + index +"                                                                        Bác sĩ/Y sĩ khám bệnh", boldFont));
                document.add(new Paragraph(STR."Toa uống:       " + tf_ngay.getText() +" ngày" +"                                                              (Ký, ghi rõ họ tên)" , regularFont));
            document.add(new Paragraph(STR."\n\nKhám lại mang theo đơn này." , footerbold));
            document.add(new Paragraph(STR."Ngày giờ in: " +LocalDate.now() +"                                                                         BS." +user.getEmployName(), footerbold));

        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            File file = new File(path);
            Desktop.getDesktop().open(file);
            document.close();
        }
    }
    private void printBangke() throws IOException {
        Document document = new Document();

        String path = STR."\\Prescription\\\{removeAccentsAndSpaces(tf_tenbn.getText())}_bangke.pdf";
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            String fontPath = "notosans-regular.ttf";
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            Font boldFont = new Font(baseFont, 13, Font.BOLD);
            Font titleBoldFont = new Font(baseFont, 17, Font.BOLD);
            Font regularFont = new Font(baseFont, 14);
            Font footerbold = new Font(baseFont, 13, Font.BOLD);

            document.add(new Paragraph("PHÒNG MẠCH TƯ", boldFont));
            document.add(new Paragraph("UIT CLINIC", boldFont));
            document.add(new Paragraph("Email: greenuitk17@gmail.com", footerbold));
            document.add(new Paragraph("Số điện thoại: 1900 1555", footerbold));
            document.add(new Paragraph("Địa chỉ:  136, Linh Trung, Thủ Đức, TP Thủ Đức", regularFont));
            document.add(new Paragraph("                                    BẢNG KÊ CHI PHÍ KHÁM BỆNH", titleBoldFont));
            document.add(new Paragraph("I. Phần hành chính:", boldFont));
            String gioitinh;
            if (rad_men.isSelected()) gioitinh = "Nam";
            else {
                gioitinh = "Nữ";
            }
            document.add(new Paragraph(STR."Mã BN:  \{tf_mabn.getText()} - Họ tên:  \{tf_tenbn.getText()} - Ngày sinh:  \{tf_ngaysinh.getText()} - Giới tính:  \{(gioitinh)}", regularFont));
            document.add(new Paragraph(STR."Triệu chứng:  \{tf_trieuChung.getText()}", regularFont));
            String maTenBenhPhu = "";
            if (!tf_maBenhPhu.getText().isEmpty())
                maTenBenhPhu = STR.";   Bệnh phụ: (\{tf_maBenhPhu.getText()}) - \{tf_tenBenhPhu.getText()} ";
            document.add(new Paragraph(STR."Chẩn đoán:   Bệnh chính: \{tf_maBenhChinh.getText()} - \{tf_tenBenhChinh.getText()}" + maTenBenhPhu, regularFont));
            document.add(new Paragraph("\nII. Phần chi phí khám bệnh: ", boldFont));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            float[] columnWidths = {1.5f, 0.5f, 0.5f, 0.5f, 0.5f};
            float[] rowWidths = {0.2f, 0.4f, 0.2f, 0.2f, 0.2f};
            table.setWidths(columnWidths);

            PdfPCell cell = new PdfPCell(new Paragraph("Nội dung", boldFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("ĐVT", boldFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("Số lượng", boldFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("Đơn giá", boldFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("Thành tiền", boldFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            int examfree = 35000;
            int totalexamfree = 35000;
            int prescibefree=0;
            for (Prescribe prescribe : tbl_chosenMedicine.getItems()) {
                prescibefree += prescribe.getThanhTien();
            }

            table.addCell(new Paragraph("1. Khám bệnh:", boldFont));
            table.addCell(new Paragraph("", regularFont));
            table.addCell(new Paragraph("", regularFont));
            table.addCell(new Paragraph("", regularFont));
            table.addCell(new Paragraph(String.valueOf(totalexamfree), boldFont));

            table.addCell(new Paragraph("   1. Công khám", regularFont));
            table.addCell(new Paragraph("Lần", regularFont));
            table.addCell(new Paragraph("1", regularFont));
            table.addCell(new Paragraph("35000", regularFont));
            table.addCell(new Paragraph(String.valueOf((int)examfree), regularFont));

            table.addCell(new Paragraph("2. Thuốc", boldFont));
            table.addCell(new Paragraph("", boldFont));
            table.addCell(new Paragraph("", boldFont));
            table.addCell(new Paragraph("", boldFont));
            table.addCell(new Paragraph(String.valueOf(prescibefree), boldFont));
            for (Prescribe prescribe : tbl_chosenMedicine.getItems()) {
                table.addCell(new Paragraph("   "+prescribe.getSothuTu()+STR.". \{prescribe.getTenThuoc()}", regularFont));
                table.addCell(new Paragraph(String.valueOf(prescribe.getTenDonViTinh()), regularFont));
                table.addCell(new Paragraph(String.valueOf(prescribe.getSoLuong()), regularFont));
                table.addCell(new Paragraph(String.valueOf((int)prescribe.getDonGia()), regularFont));
                table.addCell(new Paragraph(String.valueOf((int)prescribe.getThanhTien()), regularFont));
            }
            table.addCell(new Paragraph("Cộng", boldFont));
            table.addCell(new Paragraph("", boldFont));
            table.addCell(new Paragraph("", boldFont));
            table.addCell(new Paragraph("", boldFont));
            int total =(int)(examfree+prescibefree);
            table.addCell(new Paragraph(String.valueOf(total), boldFont));
            for (int i = 0; i < rowWidths.length; i++) {
                table.getRow(i).setMaxHeights(rowWidths[i] * table.getTotalHeight());
            }
            document.add(table);
            document.add(new Paragraph("\nTổng chi phí lần khám bệnh (làm tròn đến đơn vị đồng):            "+total+" đ",boldFont));
            LocalDate date = LocalDate.now();
            document.add(new Paragraph(STR."\n                                                                                       Ngày "+date.getDayOfMonth()+ " tháng " + date.getMonthValue() + " năm " + date.getYear(), regularFont));
            document.add(new Paragraph(STR."                           NGƯỜI LẬP BẢNG KÊ                                    NGƯỜI THU TIỀN", boldFont));
            document.add(new Paragraph(STR."                            (Ký, ghi rõ họ tên)                                 (Ký, ghi rõ họ tên) ", regularFont));



        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            File file = new File(path);
            Desktop.getDesktop().open(file);
            document.close();
        }
    }
    public static String removeAccentsAndSpaces(String input) {
        // Chuẩn hóa chuỗi, loại bỏ các dấu
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String noAccents = pattern.matcher(normalized).replaceAll("");

        // Loại bỏ các khoảng trắng thừa
        String noSpaces = noAccents.replaceAll("\\s+", "");

        return noSpaces;
    }

    public void closeDisease(MouseEvent mouseEvent) {
        if (panel_diseasesResultSearch.getLayoutY()==191) {
            tf_maBenhChinh.clear();
            tf_tenBenhChinh.clear();
        } else  {
            tf_maBenhPhu.clear();
            tf_tenBenhPhu.clear();
        }
        tbl_resultSearchDisease.setVisible(false);
    }

    public void minimizeExam(MouseEvent mouseEvent) {
        Model.getInstance().getViewFactory().minimizeStage((Stage) btnClose.getScene().getWindow());
    }
}
