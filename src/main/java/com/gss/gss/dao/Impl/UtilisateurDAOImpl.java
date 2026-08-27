package com.gss.gss.dao.Impl;

import com.gss.gss.dao.UtilisateurDAO;
import com.gss.gss.database.DatabaseConnection;
import com.gss.gss.model.Utilisateur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtilisateurDAOImpl implements UtilisateurDAO {

    @Override
    public Optional<Utilisateur> findByUsername(String username) {

        String sql = """
                SELECT id, username, password, type, statut, date_creation
                FROM utilisateurs
                WHERE username = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapResultSetToUtilisateur(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la recherche de l'utilisateur.",
                    e
            );
        }

        return Optional.empty();
    }


    @Override
    public Optional<Utilisateur> findById(int id) {

        String sql = """
                SELECT id, username, password, type, statut, date_creation
                FROM utilisateurs
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapResultSetToUtilisateur(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la recherche de l'utilisateur.",
                    e
            );
        }

        return Optional.empty();
    }


    @Override
    public List<Utilisateur> findAll() {

        List<Utilisateur> utilisateurs = new ArrayList<>();

        String sql = """
                SELECT id, username, password, type, statut, date_creation
                FROM utilisateurs
                ORDER BY id DESC
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet resultSet = ps.executeQuery()
        ) {

            while (resultSet.next()) {

                // IMPORTANT : ajouter l'objet à la liste
                utilisateurs.add(
                        mapResultSetToUtilisateur(resultSet)
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la récupération des utilisateurs.",
                    e
            );
        }

        return utilisateurs;
    }


    @Override
    public List<Utilisateur> search(String recherche) {

        List<Utilisateur> utilisateurs = new ArrayList<>();

        String sql = """
                SELECT id, username, password, type, statut, date_creation
                FROM utilisateurs
                WHERE CAST(id AS CHAR) LIKE ?
                   OR username LIKE ?
                   OR type LIKE ?
                   OR statut LIKE ?
                   OR CAST(date_creation AS CHAR) LIKE ?
                ORDER BY id DESC
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            String valeur = "%" + recherche + "%";

            ps.setString(1, valeur);
            ps.setString(2, valeur);
            ps.setString(3, valeur);
            ps.setString(4, valeur);
            ps.setString(5, valeur);

            try (ResultSet resultSet = ps.executeQuery()) {

                while (resultSet.next()) {

                    utilisateurs.add(
                            mapResultSetToUtilisateur(resultSet)
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la recherche des utilisateurs.",
                    e
            );
        }

        return utilisateurs;
    }


    @Override
    public List<Utilisateur> findByStatut(String statut) {

        List<Utilisateur> utilisateurs = new ArrayList<>();

        String sql = """
                SELECT id, username, password, type, statut, date_creation
                FROM utilisateurs
                WHERE statut = ?
                ORDER BY id DESC
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setString(1, statut);

            try (ResultSet resultSet = ps.executeQuery()) {

                while (resultSet.next()) {

                    utilisateurs.add(
                            mapResultSetToUtilisateur(resultSet)
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la recherche par statut.",
                    e
            );
        }

        return utilisateurs;
    }


    @Override
    public List<Utilisateur> findByType(String type) {

        List<Utilisateur> utilisateurs = new ArrayList<>();

        String sql = """
                SELECT id, username, password, type, statut, date_creation
                FROM utilisateurs
                WHERE type = ?
                ORDER BY id DESC
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setString(1, type);

            try (ResultSet resultSet = ps.executeQuery()) {

                while (resultSet.next()) {

                    utilisateurs.add(
                            mapResultSetToUtilisateur(resultSet)
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la recherche par type.",
                    e
            );
        }

        return utilisateurs;
    }


    @Override
    public boolean existsByUsername(String username) {

        String sql = """
                SELECT COUNT(*)
                FROM utilisateurs
                WHERE username = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la vérification du nom d'utilisateur.",
                    e
            );
        }

        return false;
    }


    @Override
    public boolean save(Utilisateur utilisateur) {

        String sql = """
                INSERT INTO utilisateurs
                (username, password, type, statut)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setString(1, utilisateur.getUsername());
            statement.setString(2, utilisateur.getPassword());
            statement.setString(3, utilisateur.getType());
            statement.setString(4, utilisateur.getStatut());

            int lignes = statement.executeUpdate();

            if (lignes > 0) {

                try (ResultSet generatedKeys =
                             statement.getGeneratedKeys()) {

                    if (generatedKeys.next()) {
                        utilisateur.setId(
                                generatedKeys.getInt(1)
                        );
                    }
                }

                // IMPORTANT
                return true;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de l'ajout de l'utilisateur.",
                    e
            );
        }

        return false;
    }


    @Override
    public boolean update(Utilisateur utilisateur) {

        String sql = """
                UPDATE utilisateurs
                SET username = ?,
                    password = ?,
                    type = ?,
                    statut = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, utilisateur.getUsername());
            statement.setString(2, utilisateur.getPassword());
            statement.setString(3, utilisateur.getType());
            statement.setString(4, utilisateur.getStatut());
            statement.setInt(5, utilisateur.getId());

            int lignes = statement.executeUpdate();

            return lignes > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la modification de l'utilisateur.",
                    e
            );
        }
    }


    @Override
    public boolean delete(int id) {

        String sql = """
                DELETE FROM utilisateurs
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            int lignes = statement.executeUpdate();

            return lignes > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la suppression de l'utilisateur.",
                    e
            );
        }
    }


    private Utilisateur mapResultSetToUtilisateur(
            ResultSet resultSet
    ) throws SQLException {

        Utilisateur utilisateur = new Utilisateur();

        utilisateur.setId(
                resultSet.getInt("id")
        );

        utilisateur.setUsername(
                resultSet.getString("username")
        );

        utilisateur.setPassword(
                resultSet.getString("password")
        );

        utilisateur.setType(
                resultSet.getString("type")
        );

        utilisateur.setStatut(
                resultSet.getString("statut")
        );

        Date date = resultSet.getDate("date_creation");

        if (date != null) {
            utilisateur.setDateCreation(
                    date.toLocalDate()
            );
        }

        return utilisateur;
    }
}