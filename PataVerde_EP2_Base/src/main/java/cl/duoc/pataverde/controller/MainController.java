
package cl.duoc.pataverde.controller;

import cl.duoc.pataverde.dao.MascotaRepository;
import cl.duoc.pataverde.model.Mascota;
import cl.duoc.pataverde.model.Perro;
import cl.duoc.pataverde.model.Gato;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.Locale;

public class MainController {

    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtEdad;
    @FXML private TextField txtPeso;
    @FXML private TextField txtRaza;

    @FXML private CheckBox chkVacunado;
    @FXML private CheckBox chkAdoptable;
    @FXML private CheckBox chkExterior;

    @FXML private Label lblMensaje;

    @FXML private TableView<Mascota> tablaMascotas;
    @FXML private TableColumn<Mascota, String> colTipo;
    @FXML private TableColumn<Mascota, String> colNombre;
    @FXML private TableColumn<Mascota, Integer> colEdad;
    @FXML private TableColumn<Mascota, Double> colPeso;
    @FXML private TableColumn<Mascota, String> colCosto;

    private final MascotaRepository repository = new MascotaRepository();

    private final ObservableList<Mascota> listaMascotas =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        cmbTipo.getItems().addAll("Perro", "Gato");

        configurarTabla();

        listaMascotas.addAll(repository.getPerros());
        listaMascotas.addAll(repository.getGatos());

        tablaMascotas.setItems(listaMascotas);

        cmbTipo.valueProperty().addListener(
                (observable, anterior, nuevo) -> actualizarCamposTipo()
        );

        actualizarCamposTipo();

        mostrarMensaje("Ingrese los datos de la mascota.");
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

    private void actualizarCamposTipo() {

        String tipo = cmbTipo.getValue();

        boolean esPerro = "Perro".equals(tipo);
        boolean esGato = "Gato".equals(tipo);

        txtRaza.setDisable(!esPerro);
        chkVacunado.setDisable(!esPerro);
        chkAdoptable.setDisable(!esPerro);

        chkExterior.setDisable(!esGato);
    }

    @FXML
    private void registrarMascota() {

        try {

            String tipo = cmbTipo.getValue();

            if (tipo == null) {
                mostrarMensaje("Debe seleccionar el tipo de mascota.");
                return;
            }

            String nombre = txtNombre.getText().trim();

            if (nombre.isEmpty()) {
                mostrarMensaje("Debe ingresar el nombre de la mascota.");
                return;
            }

            if (txtEdad.getText().trim().isEmpty()) {
                mostrarMensaje("Debe ingresar la edad de la mascota.");
                return;
            }

            if (txtPeso.getText().trim().isEmpty()) {
                mostrarMensaje("Debe ingresar el peso de la mascota.");
                return;
            }

            int edad = Integer.parseInt(txtEdad.getText().trim());

            double peso = Double.parseDouble(
                    txtPeso.getText().trim().replace(",", "."));

            if (edad < 0 || edad > 30) {
                mostrarMensaje("La edad debe estar entre 0 y 30 años.");
                return;
            }

            if (peso <= 0) {
                mostrarMensaje("El peso debe ser mayor que cero.");
                return;
            }

            if (tipo.equals("Perro")) {

                String raza = txtRaza.getText().trim();

                if (raza.isEmpty()) {
                    mostrarMensaje("Debe ingresar la raza del perro.");
                    return;
                }

                Perro perro = new Perro(
                        nombre,
                        edad,
                        peso,
                        raza,
                        chkVacunado.isSelected()
                );

                if (chkAdoptable.isSelected()) {
                    perro.marcarDisponibleParaAdopcion();
                }

                repository.agregarPerro(perro);
                listaMascotas.add(perro);

            } else if (tipo.equals("Gato")) {

                Gato gato = new Gato(
                        nombre,
                        edad,
                        peso,
                        chkExterior.isSelected()
                );

                repository.agregarGato(gato);
                listaMascotas.add(gato);
            }

            mostrarMensaje(nombre + " (" + tipo + ") registrado correctamente.");

            limpiarFormulario();

        } catch (NumberFormatException e) {

            mostrarMensaje("La edad y el peso deben contener valores numéricos válidos.");

        } catch (IllegalArgumentException e) {

            mostrarMensaje(e.getMessage());

        } catch (RuntimeException e) {

            mostrarMensaje("Error al guardar la mascota. Intente nuevamente.");
        }
    }

    @FXML
    private void eliminarMascota() {

        Mascota seleccionada = tablaMascotas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarMensaje("Debe seleccionar una mascota para eliminar.");
            return;
        }

        try {

            if (seleccionada instanceof Perro) {

                repository.eliminarPerro((Perro) seleccionada);

            } else if (seleccionada instanceof Gato) {

                repository.eliminarGato((Gato) seleccionada);
            }

            listaMascotas.remove(seleccionada);

            mostrarMensaje(seleccionada.getNombre() + " eliminado correctamente.");

        } catch (RuntimeException e) {

            mostrarMensaje("Error al eliminar la mascota.");
        }
    }

    @FXML
    private void abrirBusqueda() {

        String ruta = "/cl/duoc/pataverde/view/buscar-view.fxml";

        try {

            java.net.URL recurso = getClass().getResource(ruta);

            if (recurso == null) {
                mostrarMensaje("No se encuentra buscar-view.fxml. Revisa su ubicación.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(recurso);

            Parent vista = loader.load();

            BuscarController controlador = loader.getController();

            controlador.setMascotas(listaMascotas);

            Stage ventana = new Stage();

            ventana.setTitle("Búsqueda de mascotas");
            ventana.setScene(new Scene(vista));

            ventana.initOwner(tablaMascotas.getScene().getWindow());
            ventana.initModality(Modality.WINDOW_MODAL);

            ventana.showAndWait();

            tablaMascotas.refresh();

        } catch (Exception e) {

            mostrarMensaje(
                    "Error al abrir búsqueda: "
                            + e.getClass().getSimpleName()
                            + " - "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    private void limpiarFormulario() {

        cmbTipo.setValue(null);

        txtNombre.clear();
        txtEdad.clear();
        txtPeso.clear();
        txtRaza.clear();

        chkVacunado.setSelected(false);
        chkAdoptable.setSelected(false);
        chkExterior.setSelected(false);

        actualizarCamposTipo();
    }

    private void mostrarMensaje(String mensaje) {

        lblMensaje.setText(mensaje);
    }
}
