package com.example.privateclinic.DataAccessObject;

import com.example.privateclinic.Models.ConnectDB;
import com.example.privateclinic.Models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    User user = new User();

    public User getEmployee() {
        return user;
    }

    public void setEmployee(User _user) {
        this.user = _user;
    }

    public String GetHash(String plainText) {
        try {
            // Khởi tạo đối tượng MessageDigest với thuật toán MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Chuyển đổi chuỗi thành mảng bytes và băm bằng MD5
            byte[] messageDigest = md.digest(plainText.getBytes());

            // Chuyển đổi mảng bytes thành chuỗi hex để hiển thị kết quả
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Thuật toán MD5 không được hỗ trợ.");
            e.printStackTrace();
            return null;
        }
    }
    public int CheckValidate(String username, String password) {
        ConnectDB connectDB = new ConnectDB();
        password = GetHash(password);
        String query = "SELECT * FROM nhanvien WHERE username = '"+username+"' AND (defaultpassword = '"+password+"' OR password ='"+password+"')";
        try
        {
            //thực thi truy vấn và lấy kết qua
            ResultSet resultSet = connectDB.getData(query);
            //kiểm tra kq trả về
            if (resultSet.next()) {
                //tìm thấy người dùng có user và password khớp
                if (resultSet.getString("defaultpassword")!=null && resultSet.getString("defaultpassword").equals(password))
                    return 1;//mật khẩu mặc định
                else {
                    user.setEmployee_id(resultSet.getInt("manv"));
                    user.setEmployName(resultSet.getString("hoten"));
                    user.setPhoneNumber(resultSet.getString("sdt"));
                    user.setCitizen_id(resultSet.getString("cccd"));
                    user.setUsername(resultSet.getString("username"));
                    user.setPosition(resultSet.getString("vitri"));
                    user.setAddress(resultSet.getString("diachi"));
                    user.setEmail(resultSet.getString("email"));
                    return 2; // mật khẩu chính
                }
            } else {
                return 0; // không tìm thấy
            }
        }
        catch (SQLException e )
        {
            e.printStackTrace();
            return -1;
        }
    }
    public String getEmail(String username) throws SQLException {
        String username_result =null;
        ConnectDB connect = new ConnectDB();
        String query = "SELECT email FROM nhanvien WHERE username = '" + username +"'";
        ResultSet resultSet = connect.getData(query);
        if(resultSet.next()) // kiểm tra xem resultSet có dữ liệu hay không
        {
            username_result = resultSet.getString("email");
        }
        return username_result;
    }
    public boolean UpdatePassword(String username,String newPassword, int index) throws SQLException
    {
        newPassword = GetHash(newPassword);
        String querry;
        ConnectDB connect = new ConnectDB();
        PreparedStatement preparedStatement = null;
        if (index == 0)
        {
            querry = "UPDATE nhanvien SET password = ?, defaultpassword = NULL  WHERE username = ? ";
            preparedStatement = connect.databaseLink.prepareStatement(querry);
            preparedStatement.setString(1,newPassword);
            preparedStatement.setString(2,username);
        }
        else
        {
            querry = "UPDATE nhanvien SET password = ? WHERE username = ? ";
            preparedStatement = connect.databaseLink.prepareStatement(querry);
            preparedStatement.setString(1,newPassword);
            preparedStatement.setString(2,username);
        }
        if (connect.handleData(preparedStatement))
        {
            return true;
        }
        else
            return false;
    }
    final String LOWER_CASE = "abcdefghijklmnopqursuvwxyz";
    final String  UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    final String  NUMBERS = "123456789";
    final String  SPECIALS = "!@£$%^&*()#€";

    public String GeneratePassword(boolean useLowercase, boolean useUppercase, boolean useNumbers, boolean useSpecial, int passwordSize)
    {
        char[] _password = new char[passwordSize];
        String charSet = "";
        SecureRandom random = new SecureRandom();
        int counter= 0;
        if (useLowercase) charSet += LOWER_CASE;

        if (useUppercase) charSet += UPPER_CASE;
        if (useNumbers) charSet += NUMBERS;

        if (useSpecial) charSet += SPECIALS;

        for (counter = 0; counter < passwordSize; counter++) {
            _password[counter] = charSet.charAt(random.nextInt(charSet.length()));
        }

        return new String(_password);
    }

    public String getUsername(String _username)
    {
        String username =null;
        ConnectDB connect = new ConnectDB();
        String query = "SELECT username FROM nhanvien WHERE username = '" + _username +"'";
        ResultSet resultSet = connect.getData(query);
        try
        {
            if(resultSet.next()) // kiểm tra xem resultSet có dữ liệu hay không
            {
                username = resultSet.getString("username");
            }
            return username;
        }
        catch (SQLException e )
        {
            e.printStackTrace();
        }
        return null;
    }
    /*public Boolean AddEmployee(String name, String citizen_id, String address, String phone, String email, String position) throws SQLException
    {
        ConnectDB connectDB = new ConnectDB();
        String _pass = GeneratePassword(true, true, true, false, 6);
        String query = "INSERT INTO employee (hoten,cccd, diachi,sdt,email,vitri,username, defaultpassword) VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = connectDB.getConnection().prepareStatement(query);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, citizen_id);
        preparedStatement.setString(3, address);
        preparedStatement.setString(4, phone);
        preparedStatement.setString(5, email);
        preparedStatement.setString(6, position);
        preparedStatement.setString(7, email);
        preparedStatement.setString(8, _pass);

        if (connectDB.handleData(preparedStatement)) {
            return true;
        }
        return false;

    }*/
    /*public Boolean UpdateEmployee(String id, String name, String citizen_id, String address, String phone, String email,String username, String position) throws SQLException
    {
        ConnectDB connectDB = new ConnectDB();

        String _pass = GeneratePassword(true,true,true,false,6);
        String query = "UPDATE employee SET HoTen = ?,CCCD =?, DiaChi,SDT = ?,Email=?,ViTri=?,TK=? WHERE MaNV = ?)";
        PreparedStatement preparedStatement = connectDB.getConnection().prepareStatement(query);
        preparedStatement.setString(1,name);
        preparedStatement.setString(2,citizen_id);
        preparedStatement.setString(3,address);
        preparedStatement.setString(4,phone);
        preparedStatement.setString(5,email);
        preparedStatement.setString(6,position);
        preparedStatement.setString(7,username);
        preparedStatement.setString(8,id);

        if(connectDB.handleData(preparedStatement))
        {
            return true;
        }
        return false;
    }*/
}
