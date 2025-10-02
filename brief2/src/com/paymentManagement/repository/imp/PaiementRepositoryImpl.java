package com.paymentManagement.repository.imp;

import com.paymentManagement.config.database.DatabaseConnection;
import com.paymentManagement.model.entity.Paiement;
import com.paymentManagement.model.enums.TypePaiement;
import com.paymentManagement.repository.interfaces.PaiementRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaiementRepositoryImpl implements PaiementRepository {

    @Override
    public void save(Paiement paiement) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "INSERT INTO paiements (type_paiement, montant, date_paiement, motif, evenement, condition_validee, id_agent) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setObject(1, paiement.getTypePaiement().name(), Types.OTHER);
            stmt.setBigDecimal(2, paiement.getMontant());
            stmt.setDate(3, Date.valueOf(paiement.getDatePaiement()));
            stmt.setString(4, paiement.getMotif());
            stmt.setString(5, paiement.getEvenement());
            stmt.setBoolean(6, paiement.isConditionValidee());
            stmt.setInt(7, paiement.getIdAgent());

            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                paiement.setIdPaiement(generatedKeys.getInt(1));
                System.out.println("Paiement saved with ID: " + paiement.getIdPaiement());
            }

            generatedKeys.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Error saving paiement: " + e.getMessage());
            throw new RuntimeException("Failed to save paiement", e);
        }
    }

    @Override
    public Paiement findById(Integer id) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT * FROM paiements WHERE id_paiement = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);

            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                Paiement paiement = createPaiementFromResultSet(resultSet);
                resultSet.close();
                stmt.close();
                return paiement;
            }

            resultSet.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Error finding paiement: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Paiement> findAll() {
        List<Paiement> paiements = new ArrayList<>();

        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT * FROM paiements ORDER BY date_paiement DESC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Paiement paiement = createPaiementFromResultSet(resultSet);
                paiements.add(paiement);
            }

            resultSet.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Error fetching paiements: " + e.getMessage());
        }

        return paiements;
    }

    @Override
    public List<Paiement> findByAgentId(Integer agentId) {
        List<Paiement> paiements = new ArrayList<>();

        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT * FROM paiements WHERE id_agent = ? ORDER BY date_paiement DESC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, agentId);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Paiement paiement = createPaiementFromResultSet(resultSet);
                paiements.add(paiement);
            }

            resultSet.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Error finding paiements by agent: " + e.getMessage());
        }

        return paiements;
    }

    @Override
    public List<Paiement> findByTypePaiement(TypePaiement typePaiement) {
        List<Paiement> paiements = new ArrayList<>();

        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT * FROM paiements WHERE type_paiement = ? ORDER BY date_paiement DESC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setObject(1, typePaiement.name(), Types.OTHER);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Paiement paiement = createPaiementFromResultSet(resultSet);
                paiements.add(paiement);
            }

            resultSet.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Error finding paiements by type: " + e.getMessage());
        }

        return paiements;
    }

    @Override
    public void update(Paiement paiement) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "UPDATE paiements SET type_paiement = ?, montant = ?, date_paiement = ?, " +
                    "motif = ?, evenement = ?, condition_validee = ? WHERE id_paiement = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setObject(1, paiement.getTypePaiement().name(), Types.OTHER);
            stmt.setBigDecimal(2, paiement.getMontant());
            stmt.setDate(3, Date.valueOf(paiement.getDatePaiement()));
            stmt.setString(4, paiement.getMotif());
            stmt.setString(5, paiement.getEvenement());
            stmt.setBoolean(6, paiement.isConditionValidee());
            stmt.setInt(7, paiement.getIdPaiement());

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Paiement updated successfully");
            }

            stmt.close();

        } catch (SQLException e) {
            System.out.println("Error updating paiement: " + e.getMessage());
            throw new RuntimeException("Failed to update paiement", e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String sql = "DELETE FROM paiements WHERE id_paiement = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();

            stmt.close();

            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting paiement: " + e.getMessage());
            return false;
        }
    }

    private Paiement createPaiementFromResultSet(ResultSet resultSet) throws SQLException {
        Integer idPaiement = resultSet.getInt("id_paiement");
        String typePaiementStr = resultSet.getString("type_paiement");
        TypePaiement typePaiement = TypePaiement.valueOf(typePaiementStr);
        BigDecimal montant = resultSet.getBigDecimal("montant");
        LocalDate datePaiement = resultSet.getDate("date_paiement").toLocalDate();
        String motif = resultSet.getString("motif");
        String evenement = resultSet.getString("evenement");
        boolean conditionValidee = resultSet.getBoolean("condition_validee");
        Integer idAgent = resultSet.getInt("id_agent");

        return new Paiement(idPaiement, typePaiement, montant, datePaiement, motif,
                evenement, conditionValidee, idAgent);
    }
}