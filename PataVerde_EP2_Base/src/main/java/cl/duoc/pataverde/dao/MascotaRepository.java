
package cl.duoc.pataverde.dao;

import cl.duoc.pataverde.model.Gato;
import cl.duoc.pataverde.model.Perro;

import java.util.ArrayList;
import java.util.List;

public class MascotaRepository {

    private final MascotaDAO dao;

    private final List<Perro> perros;
    private final List<Gato> gatos;

    public MascotaRepository() {
        this.dao = new JsonMascotaDAO();
        this.perros = dao.cargarPerros();
        this.gatos = dao.cargarGatos();
    }

    public List<Perro> getPerros() {
        return perros;
    }

    public List<Gato> getGatos() {
        return gatos;
    }

    public void agregarPerro(Perro perro) {
        perros.add(perro);
        guardar();
    }

    public void agregarGato(Gato gato) {
        gatos.add(gato);
        guardar();
    }

    public void eliminarPerro(Perro perro) {
        perros.remove(perro);
        guardar();
    }

    public void eliminarGato(Gato gato) {
        gatos.remove(gato);
        guardar();
    }

    public void guardar() {
        dao.guardarPerros(perros);
        dao.guardarGatos(gatos);
    }
}
