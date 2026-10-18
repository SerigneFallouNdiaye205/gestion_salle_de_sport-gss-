package com.gss.gss.util;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public final class StatusCellFactory {

    private static final Color ALERT_RED = Color.web("#c62828");

    private StatusCellFactory() {}

    public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> redInactiveOrExpired() {
        return column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                setText(empty || status == null ? null : status);
                setTextFill(!empty && status != null
                        && ("EXPIRE".equalsIgnoreCase(status)
                        || "INACTIF".equalsIgnoreCase(status))
                        ? ALERT_RED : null);
                setStyle(!empty && status != null
                        && ("EXPIRE".equalsIgnoreCase(status)
                        || "INACTIF".equalsIgnoreCase(status))
                        ? "-fx-font-weight: bold;" : "");
            }
        };
    }

    public static void colorStatusOptions(ComboBox<String> comboBox) {
        comboBox.setCellFactory(list -> statusListCell());
        comboBox.setButtonCell(statusListCell());
    }

    private static ListCell<String> statusListCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                setText(empty || status == null ? null : status);
                setTextFill(!empty && isAlertStatus(status) ? ALERT_RED : null);
                setStyle(!empty && isAlertStatus(status) ? "-fx-font-weight: bold;" : "");
            }
        };
    }

    private static boolean isAlertStatus(String status) {
        return "EXPIRE".equalsIgnoreCase(status) || "INACTIF".equalsIgnoreCase(status);
    }
}
