package com.saltyfirm.saltyfirm.Repositories;

import com.saltyfirm.saltyfirm.Models.Firm;
import com.saltyfirm.saltyfirm.Repositories.DatabaseConnection.DbHandler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Victor Petersen | Patrick Jønsson
@Service
public class FirmRepositoryImpl implements FirmRepository {

    @Autowired
    DbHandler dbHandler;

    private final org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Firm> searchFirms(String word) {
        log.info("Searching for a firm");

        List<Firm> firmList = new ArrayList<>();

        try{
            Connection connection = dbHandler.createConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM saltyfirm.firm WHERE firm_name LIKE ?");

            preparedStatement.setString(1, "%" + word + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                Firm firm = new Firm();

                firm.setFirmId(resultSet.getInt("firm_id"));
                firm.setFirmName(resultSet.getString("firm_name"));
                firm.setFirmType(resultSet.getString("firm_type"));
                firm.setOverallScore(resultSet.getDouble("overall_score"));
                firm.setDescription(resultSet.getString("description"));
                firm.setLogoURL(resultSet.getString("logo_url"));

                firmList.add(firm);
            }

        } catch (SQLException e){
            log.warn("Found SQLException: ");
            e.printStackTrace();
        }

        return firmList;
    }

    @Override
    public Firm findFirmById(int firmId) {
        log.info("Finding firm by id");
        try {
            Connection connection = dbHandler.createConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM saltyfirm.firm WHERE firm_id = ?");

            preparedStatement.setInt(1, firmId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                Firm firm = new Firm();

                firm.setFirmId(resultSet.getInt("firm_id"));
                firm.setFirmName(resultSet.getString("firm_name"));
                firm.setFirmType(resultSet.getString("firm_type"));
                firm.setOverallScore(resultSet.getDouble("overall_score"));
                firm.setDescription(resultSet.getString("description"));
                firm.setLogoURL(resultSet.getString("logo_url"));
                return firm;
            } else {
                log.info("Didnt find anything");
            }
            connection.close();

        } catch (SQLException e) {
            log.warn("Found SQLException: ");
            e.printStackTrace();
        }
        log.info("No firm found");
        return null;
    }

    @Override
    public int deleteFirm(int firmId) {
        log.info("Deleting firm");
        try {
            log.info("Executing deleteFirm");
            Connection connection = dbHandler.createConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM saltyfirm.firm WHERE firm_id = ?");
            preparedStatement.setInt(1, firmId);

            log.info("Executed deleteFirm");
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.warn("Found SQLException: ");
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int editFirm(Firm firm) {
        log.info("Editing firm");
        try{
            Connection connection = dbHandler.createConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE saltyfirm.firm SET firm_name = ?, firm_type = ?, description = ?, logo_url = ? WHERE firm_id = ?");
            preparedStatement.setString(1, firm.getFirmName());
            preparedStatement.setString(2, firm.getFirmType());
            preparedStatement.setString(3, firm.getDescription());
            preparedStatement.setString(4, firm.getLogoURL());
            preparedStatement.setInt(5, firm.getFirmId());

            return preparedStatement.executeUpdate();

        } catch (SQLException e){
            log.warn("Found SQLException: ");
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public double getFirmTotalScore(int firmId) {
        log.info("Fetching total firm score");
        try {
            Connection connection = dbHandler.createConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT department_score FROM saltyfirm.department WHERE firm_fk_id = ?");
            preparedStatement.setInt(1, firmId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                double totalFirmScore = resultSet.getInt(1);

                return totalFirmScore;
            }

            }catch (SQLException e){
            log.warn("Found SQLException: ");
                e.printStackTrace();
        }
        return 0;
    }


    @Override
    public List<Firm> getAllFirms(){
        log.info("Fetching all firms");
        List<Firm> firmList = new ArrayList<>();
        try {
            Connection connection = dbHandler.createConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * from saltyfirm.firm");

            while(resultSet.next()){
                Firm firm = new Firm();
                firm.setFirmId(resultSet.getInt("firm_id"));
                firm.setFirmName(resultSet.getString("firm_name"));
                firm.setFirmType(resultSet.getString("firm_type"));
                firm.setOverallScore(resultSet.getDouble("overall_score"));
                firm.setDescription(resultSet.getString("description"));
                firm.setLogoURL(resultSet.getString("logo_url"));
                firmList.add(firm);

            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return firmList;
    }

}
