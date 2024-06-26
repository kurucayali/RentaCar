package dao;

import core.Db;
import entity.Brand;
import entity.Model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class ModelDao {
    private Connection con;
    private final BrandDao brandDao = new BrandDao();

    public ModelDao() {
        this.con = Db.getInstance();
    }

    public Model getById(int id) {
        Model obj = null;
        String query = "select * from public.model where model_id = ?";
        try {
            PreparedStatement pr = con.prepareStatement(query);
            pr.setInt(1, id);
            ResultSet rs = pr.executeQuery();
            if (rs.next()) obj = this.match(rs);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return obj;
    }

    public ArrayList<Model> findAll() {
        return this.selectByQuery("select * from public.model order by model_id asc ");
    }
    public ArrayList<Model> getByListBrandId(int brandId) {
        return this.selectByQuery("select * from public.model where model_brand_id = " + brandId);
    }

    public ArrayList<Model> selectByQuery(String query) {
        ArrayList<Model> modelList = new ArrayList<>();
        try {
            ResultSet rs = this.con.createStatement().executeQuery(query);
            while (rs.next()) {
                modelList.add(this.match(rs));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return modelList;
    }

    public boolean save(Model model) {
        String query = "insert into public.model" +
                "(" +
                "model_brand_id," +
                "model_name," +
                "model_type," +
                "model_year," +
                "model_fuel," +
                "model_gear" +
                ")" +
                "values (?,?,?,?,?,?,)";
        try {
            PreparedStatement pr = con.prepareStatement(query);
            pr.setInt(1, model.getBrand_id());
            pr.setString(2, model.getName());
            pr.setString(3, model.getType().toString());
            pr.setString(4, model.getYear());
            pr.setString(5, model.getFuel().toString());
            pr.setString(6, model.getGear().toString());
            return pr.executeUpdate() != -1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    public boolean update(Model model) {
        String query = "update public.model set" +
                "model_brand_id = ? ," +
                "model_name = ? ," +
                "model_type = ? ," +
                "model_year = ? ," +
                "model_fuel = ? ," +
                "model_gear = ?" +
                "where model_id = ? ";
        try {
            PreparedStatement pr = con.prepareStatement(query);
            pr.setInt(1, model.getBrand_id());
            pr.setString(2, model.getName());
            pr.setString(3, model.getType().toString());
            pr.setString(4, model.getYear());
            pr.setString(5, model.getFuel().toString());
            pr.setString(6, model.getGear().toString());
            pr.setInt(7, model.getId());
            return pr.executeUpdate() != -1;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return true;
    }

    public boolean delete(int model_id) {
        String query = "delete from public.model where model_id=?";
        try {
            PreparedStatement pr = con.prepareStatement(query);
            pr.setInt(1, model_id);
            return pr.executeUpdate() != -1;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return true;
    }


    public Model match(ResultSet rs) throws SQLException {
        Model model = new Model();

        model.setId(rs.getInt("model_id"));
        model.setName(rs.getString("model_name"));
        model.setFuel(Model.Fuel.valueOf(rs.getString("model_fuel")));
        model.setGear(Model.Gear.valueOf(rs.getString("model_gear")));
        model.setType(Model.Type.valueOf(rs.getString("model_type")));
        model.setYear(rs.getString("model_year"));
        model.setBrand(this.brandDao.getById(rs.getInt("model_brand_id")));
        model.setBrand_id(rs.getInt("model_brand_id"));

        return model;
    }

}

