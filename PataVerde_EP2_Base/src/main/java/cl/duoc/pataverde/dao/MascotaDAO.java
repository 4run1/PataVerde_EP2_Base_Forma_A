
package cl.duoc.pataverde.dao;

import cl.duoc.pataverde.model.Gato;
import cl.duoc.pataverde.model.Perro;

import java.util.List;

public interface MascotaDAO {

    List<Perro> cargarPerros();

    void guardarPerros(List<Perro> perros);

    List<Gato> cargarGatos();

    void guardarGatos(List<Gato> gatos);
}
