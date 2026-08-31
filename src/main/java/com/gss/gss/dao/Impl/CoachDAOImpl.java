package com.gss.gss.dao.Impl;

import com.gss.gss.dao.CoachDAO;
import com.gss.gss.database.DatabaseConnection;
import com.gss.gss.model.Coach;
import com.gss.gss.security.PasswordHasher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoachDAOImpl implements CoachDAO {

    private static final String MOT_DE_PASSE_DEFAUT =
            "gss";

    @Override
    public List<Coach> findAll() {

        List<Coach> coachs = new ArrayList<>();

        String sql = """
                SELECT id,
                       nom,
                       prenom,
                       telephone,
                       email,
                       specialite,
                       salaire,
                       disponibilite
                FROM coachs
                ORDER BY id DESC
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        connection.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {
                coachs.add(convertir(rs));
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erreur lors de la récupération des coachs.",
                    e
            );
        }

        return coachs;
    }

    @Override
    public Coach findById(int id) {

        String sql = """
                SELECT id,
                       nom,
                       prenom,
                       telephone,
                       email,
                       specialite,
                       salaire,
                       disponibilite
                FROM coachs
                WHERE id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        connection.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return convertir(rs);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erreur lors de la recherche du coach.",
                    e
            );
        }

        return null;
    }

    @Override
    public List<Coach> search(String recherche) {

        List<Coach> coachs = new ArrayList<>();

        String sql = """
                SELECT id,
                       nom,
                       prenom,
                       telephone,
                       email,
                       specialite,
                       salaire,
                       disponibilite
                FROM coachs
                WHERE CAST(id AS CHAR) LIKE ?
                   OR nom LIKE ?
                   OR prenom LIKE ?
                   OR telephone LIKE ?
                   OR email LIKE ?
                   OR specialite LIKE ?
                   OR disponibilite LIKE ?
                ORDER BY id DESC
                """;

        String valeur = "%" + recherche + "%";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        connection.prepareStatement(sql)
        ) {

            for (int i = 1; i <= 7; i++) {
                ps.setString(i, valeur);
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    coachs.add(convertir(rs));
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erreur lors de la recherche des coachs.",
                    e
            );
        }

        return coachs;
    }

    @Override
    public boolean save(Coach coach) {

        String sqlUtilisateur = """
            INSERT INTO utilisateurs
            (
                username,
                password,
                type,
                statut
            )
            VALUES (?, ?, ?, ?)
            """;

        String sqlCoach = """
            INSERT INTO coachs
            (
                id,
                nom,
                prenom,
                telephone,
                email,
                specialite,
                salaire,
                disponibilite
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        Connection connection = null;

        try {

            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            /*
             * 1. Création du compte utilisateur
             */

            String username = "coach" + System.currentTimeMillis();

            String passwordHash =
                    PasswordHasher.hash(MOT_DE_PASSE_DEFAUT);

            int utilisateurId;

            try (
                    PreparedStatement psUtilisateur =
                            connection.prepareStatement(
                                    sqlUtilisateur,
                                    Statement.RETURN_GENERATED_KEYS
                            )
            ) {

                psUtilisateur.setString(1, username);
                psUtilisateur.setString(2, passwordHash);
                psUtilisateur.setString(3, "COACH");
                psUtilisateur.setString(4, "ACTIF");

                int lignes = psUtilisateur.executeUpdate();

                if (lignes == 0) {
                    connection.rollback();
                    return false;
                }

                /*
                 * Récupération de l'id de l'utilisateur
                 */

                try (
                        ResultSet generatedKeys =
                                psUtilisateur.getGeneratedKeys()
                ) {

                    if (!generatedKeys.next()) {

                        connection.rollback();

                        throw new SQLException(
                                "Impossible de récupérer l'id de l'utilisateur."
                        );
                    }

                    utilisateurId =
                            generatedKeys.getInt(1);
                }
            }

            /*
             * 2. Création du coach
             *
             * Le id du coach = id de l'utilisateur
             */

            try (
                    PreparedStatement psCoach =
                            connection.prepareStatement(sqlCoach)
            ) {

                psCoach.setInt(
                        1,
                        utilisateurId
                );

                psCoach.setString(
                        2,
                        coach.getNom()
                );

                psCoach.setString(
                        3,
                        coach.getPrenom()
                );

                psCoach.setString(
                        4,
                        coach.getTelephone()
                );

                if (coach.getEmail() == null ||
                        coach.getEmail().isBlank()) {

                    psCoach.setNull(
                            5,
                            Types.VARCHAR
                    );

                } else {

                    psCoach.setString(
                            5,
                            coach.getEmail()
                    );
                }

                if (coach.getSpecialite() == null ||
                        coach.getSpecialite().isBlank()) {

                    psCoach.setNull(
                            6,
                            Types.VARCHAR
                    );

                } else {

                    psCoach.setString(
                            6,
                            coach.getSpecialite()
                    );
                }

                if (coach.getSalaire() == null) {

                    psCoach.setNull(
                            7,
                            Types.DECIMAL
                    );

                } else {

                    psCoach.setDouble(
                            7,
                            coach.getSalaire()
                    );
                }

                psCoach.setString(
                        8,
                        coach.getDisponibilite()
                );

                int lignes =
                        psCoach.executeUpdate();

                if (lignes == 0) {

                    connection.rollback();

                    return false;
                }
            }

            /*
             * 3. Synchronisation de l'id dans l'objet Coach
             */

            coach.setId(utilisateurId);

            /*
             * 4. Tout est OK
             */

            connection.commit();

            System.out.println(
                    "Coach créé avec succès."
            );

            System.out.println(
                    "Username : " + username
            );

            System.out.println(
                    "Mot de passe initial : " +
                            MOT_DE_PASSE_DEFAUT
            );

            return true;

        } catch (SQLException e) {

            if (connection != null) {

                try {
                    connection.rollback();

                } catch (SQLException rollbackException) {
                    rollbackException.printStackTrace();
                }
            }

            throw new RuntimeException(
                    "Erreur lors de l'ajout du coach et de son compte utilisateur.",
                    e
            );

        } finally {

            if (connection != null) {

                try {

                    connection.setAutoCommit(true);
                    connection.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean update(Coach coach) {

        String sql = """
                UPDATE coachs
                SET nom = ?,
                    prenom = ?,
                    telephone = ?,
                    email = ?,
                    specialite = ?,
                    salaire = ?,
                    disponibilite = ?
                WHERE id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        connection.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    coach.getNom()
            );

            ps.setString(
                    2,
                    coach.getPrenom()
            );

            ps.setString(
                    3,
                    coach.getTelephone()
            );

            if (coach.getEmail() == null ||
                    coach.getEmail().isBlank()) {

                ps.setNull(
                        4,
                        Types.VARCHAR
                );

            } else {

                ps.setString(
                        4,
                        coach.getEmail()
                );
            }

            if (coach.getSpecialite() == null ||
                    coach.getSpecialite().isBlank()) {

                ps.setNull(
                        5,
                        Types.VARCHAR
                );

            } else {

                ps.setString(
                        5,
                        coach.getSpecialite()
                );
            }

            if (coach.getSalaire() == null) {

                ps.setNull(
                        6,
                        Types.DECIMAL
                );

            } else {

                ps.setDouble(
                        6,
                        coach.getSalaire()
                );
            }

            ps.setString(
                    7,
                    coach.getDisponibilite()
            );

            ps.setInt(
                    8,
                    coach.getId()
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erreur lors de la modification du coach.",
                    e
            );
        }
    }

    @Override
    public boolean delete(int id) {

        String sqlUtilisateur = """
                DELETE FROM utilisateurs
                WHERE username = ?
                  AND type = 'COACH'
                """;

        String sqlCoach = """
                DELETE FROM coachs
                WHERE id = ?
                """;

        Connection connection = null;

        try {

            connection =
                    DatabaseConnection.getConnection();

            connection.setAutoCommit(false);

            /*
             * 1. Suppression du compte utilisateur
             */
            try (
                    PreparedStatement ps =
                            connection.prepareStatement(
                                    sqlUtilisateur
                            )
            ) {

                ps.setString(
                        1,
                        "coach" + id
                );

                ps.executeUpdate();
            }

            /*
             * 2. Suppression du coach
             */
            int lignes;

            try (
                    PreparedStatement ps =
                            connection.prepareStatement(
                                    sqlCoach
                            )
            ) {

                ps.setInt(1, id);

                lignes = ps.executeUpdate();
            }

            if (lignes == 0) {

                connection.rollback();

                return false;
            }

            connection.commit();

            return true;

        } catch (SQLException e) {

            if (connection != null) {

                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    rollbackException.printStackTrace();
                }
            }

            throw new RuntimeException(
                    "Erreur lors de la suppression du coach.",
                    e
            );

        } finally {

            if (connection != null) {

                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Coach convertir(ResultSet rs)
            throws SQLException {

        Coach coach = new Coach();

        coach.setId(
                rs.getInt("id")
        );

        coach.setNom(
                rs.getString("nom")
        );

        coach.setPrenom(
                rs.getString("prenom")
        );

        coach.setTelephone(
                rs.getString("telephone")
        );

        coach.setEmail(
                rs.getString("email")
        );

        coach.setSpecialite(
                rs.getString("specialite")
        );

        double salaire =
                rs.getDouble("salaire");

        if (rs.wasNull()) {
            coach.setSalaire(null);
        } else {
            coach.setSalaire(salaire);
        }

        coach.setDisponibilite(
                rs.getString("disponibilite")
        );

        return coach;
    }
}