package com.example.cotacaoibov;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import static javafx.application.Application.launch;
import java.util.List;



public class Front extends Application {

    @Override
    public void start(Stage primaryStage){
        //config das interfaces
        primaryStage.setTitle("Cotações das ações da TSFbov");

        Label label = new Label("Cotacoes: ");
        ListView<String> listView = new ListView<>();
        Button btnAtualizar = new Button("Atualizar Cotações");

        //acao do botao
        btnAtualizar.setOnAction(event -> {
            //simula obtencao de cotação e as add a listview
            List<String> cotaccoes = App.obterCotacoes();
            listView.getItems().clear();
            listView.getItems().addAll(cotaccoes);
        });

        //layout da janela
        VBox vbox = new VBox(10, label, listView, btnAtualizar);
        vbox.setPadding(new Insets(15));
        Scene scene = new Scene(vbox,400,300);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }



}
