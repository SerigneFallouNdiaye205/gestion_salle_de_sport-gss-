package com.gss.gss.controller;

import com.gss.gss.util.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegistrationController {

    @FXML
    private TextField registerFirstname;
    @FXML
    private TextField registerLastname;
    @FXML
    private TextField registerEmail;
    @FXML
    private PasswordField registerPassword;
    @FXML
    private Button registerButton;
    @FXML
    private Button moveLoggin;


    @FXML
    private void register(){}
    @FXML
    public void gotoLoggin(ActionEvent event) throws IOException {
        NavigationUtil nav = new NavigationUtil();
        nav.navigate(event,"/com/gss/gss/fxml/login.fxml");
    }
}
