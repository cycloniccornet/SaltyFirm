package com.saltyfirm.saltyfirm.Repositories;

import com.saltyfirm.saltyfirm.Models.Department;
import com.saltyfirm.saltyfirm.Models.Review;
import com.saltyfirm.saltyfirm.Repositories.DatabaseConnection.DbHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Victor Petersen | Patrick Jønsson
@Repository("DepartmentRepositoryImpl")
public class DepartmentRepositoryImpl implements DepartmentRepository {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DbHandler dbHandler;

    @Override
    public Department findDepartmentById(int departmentId) {
        log.info("Finding department via id");
        try {
            Connection connection = dbHandler.createConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM saltyfirm.department WHERE department_id = ?");
            preparedStatement.setInt(1, departmentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Department department = new Department();
                department.setDepartmentId(resultSet.getInt("department_id"));
                department.setDepartmentName(resultSet.getString("department_name"));
                department.setDepartmentAddress(resultSet.getString("department_address"));
                department.setDepartmentScore(resultSet.getDouble("department_score"));
                return department;
            }

        } catch (SQLException e) {
            log.warn("Found SQLException: ");
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public List<Department> getDepartments(int firmId) {
        log.info("Fetching all departments");
        List<Department> departmentsList = new ArrayList<>();
        try {
            Connection connection = dbHandler.createConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM saltyfirm.department WHERE firm_fk_id = ?");
            preparedStatement.setInt(1, firmId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Department currentDepartment = new Department();

                currentDepartment.setDepartmentId(resultSet.getInt("department_id"));
                currentDepartment.setDepartmentName(resultSet.getString("department_name"));
                currentDepartment.setDepartmentAddress(resultSet.getString("department_address"));
                currentDepartment.setDepartmentScore(resultSet.getDouble("department_score"));

                departmentsList.add(currentDepartment);
            }
            connection.close();
            return departmentsList;

        } catch (SQLException e) {
            log.warn("Found SQLException: ");
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public int deleteDepartment(int departmentId) {
        log.info("Deleting department");
        try {
            Connection connection = dbHandler.createConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM saltyfirm.department WHERE department_id = ?");
            preparedStatement.setInt(1, departmentId);
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            log.warn("Found SQLException: ");
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public int editDepartment(Department department) {
        log.info("Editing department");
        try {
            Connection connection = dbHandler.createConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE saltyfirm.department SET department_name = ?, department_address = ? WHERE department_id = ?");
            preparedStatement.setString(1, department.getDepartmentName());
            preparedStatement.setString(2, department.getDepartmentAddress());
            preparedStatement.setInt(3, department.getDepartmentId());
            preparedStatement.executeUpdate();
            connection.close();

        } catch (SQLException e) {
            log.warn("Found SQLException: ");
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @author
     *  Takes the average from the 5 inde individual cell, add them together
     *  and devides them with the number og cells used to get the average score
     *  for a department
     *
     * @param departmentId
     *
     * @throws SQLException
     * @return 0
     */
    @Override
    public Review getRealDepartmentScores(int departmentId) {
        log.info("Fetching department scores");
        try {
            Connection connection = dbHandler.createConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT AVG(pension_scheme), AVG(benefits), AVG(management), AVG(work_environment), AVG(flexibility), AVG((pension_scheme + benefits + management + work_environment + flexibility)/5) AS overall_score\n" +
                                                                                   "FROM saltyfirm.review\n" +
                                                                                   "WHERE department_fk_id = ?;");
            preparedStatement.setInt(1, departmentId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Review review = new Review();
                review.setPensionScheme(resultSet.getDouble(1));
                review.setBenefits(resultSet.getDouble(2));
                review.setManagement(resultSet.getDouble(3));
                review.setWorkEnvironment(resultSet.getDouble(4));
                review.setFlexibility(resultSet.getDouble(5));
                review.setDepartmentOverallScore(resultSet.getDouble("overall_score"));

                return review;
            }
        } catch (SQLException e) {
            log.warn("Found SQLException: ");
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public int updateDepartmentScore(int departmentId) {
        log.info("Updating department score");
        Review review = getRealDepartmentScores(departmentId);
        review.getDepartmentOverallScore();
        try {
            Connection connection = dbHandler.createConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE saltyfirm.department SET department_score = ? WHERE department_id = ?");
            preparedStatement.setDouble(1, review.getDepartmentOverallScore());
            preparedStatement.setInt(2, departmentId);
            preparedStatement.executeUpdate();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
