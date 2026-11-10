package cl.duoc.pataverde.controller;

import cl.duoc.pataverde.model.Mascota;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.text.NumberFormat;
import java.util.Locale;

public class BuscarController {

    @FXML private TextField txtBuscar;

    @FXML private Button btnTodos;
    @FXML private Button btnPerro;
    @FXML private Button btnGato;

    @FXML private TableView<Mascota> tablaBusqueda;

    @FXML private TableColumn<Mascota, String> colTipo;
    @FXML private TableColumn<Mascota, String> colNombre;
    @FXML private TableColumn<Mascota, Integer> colEdad;
    @FXML private TableColumn<Mascota, Double> colPeso;
    @FXML private TableColumn<Mascota, String> colCosto;

    private FilteredList<Mascota> mascotasFiltradas;

    private String tipoSeleccionado = "Todos";

    @FXML
    public void initialize() {

        configurarTabla();

        txtBuscar.textProperty().addListener(
                (observable, anterior, nuevo) -> aplicarFiltro()
        );
    }

    private void configurarTabla() {

        colTipo.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(datos.getValue().getTipo()));

        colNombre.setCellValueFactory(datos ->
                new ReadOnlyStringWrapper(datos.getValue().getNombre()));

        colEdad.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getEdad()));

        colPeso.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getPeso()));

        colCosto.setCellValueFactory(datos -> {

            NumberFormat formato = NumberFormat.getCurrencyInstance(
                    new Locale("es", "CL"));

            return new ReadOnlyStringWrapper(
                    formato.format(datos.getValue().calcularCostoConsulta()));
        });
    }

    public void setMascotas(ObservableList<Mascota> mascotas) {

        mascotasFiltradas = new FilteredList<>(mascotas, mascota -> true);

        tablaBusqueda.setItems(mascotasFiltradas);

        aplicarFiltro();
    }

    @FXML
    private void filtrarTodos() {

        tipoSeleccionado = "Todos";

        aplicarFiltro();
    }

    @FXML
    private void filtrarPerros() {

        tipoSeleccionado = "Perro";

        aplicarFiltro();
    }

    @FXML
    private void filtrarGatos() {

        tipoSeleccionado = "Gato";

        aplicarFiltro();
    }

    private void aplicarFiltro() {

        if (mascotasFiltradas == null) {
            return;
        }

        String busqueda = txtBuscar.getText() == null
                ? ""
                : txtBuscar.getText().trim().toLowerCase(Locale.ROOT);

        mascotasFiltradas.setPredicate(mascota -> {

            boolean coincideTipo = tipoSeleccionado.equals("Todos")
                    || mascota.getTipo().equalsIgnoreCase(tipoSeleccionado);

            boolean coincideNombre = mascota.getNombre()
                    .toLowerCase(Locale.ROOT)
                    .contains(busqueda);

            return coincideTipo && coincideNombre;
        });
    }
}
