
package cl.duoc.pataverde.dao;

import cl.duoc.pataverde.model.Gato;
import cl.duoc.pataverde.model.Perro;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JsonMascotaDAO implements MascotaDAO {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final Path archivoPerros = Path.of("perros.json");
    private final Path archivoGatos = Path.of("gatos.json");

    @Override
    public List<Perro> cargarPerros() {
        if (!Files.exists(archivoPerros)) {
            return new ArrayList<>();
        }

        try {
            String json = Files.readString(archivoPerros);
            Type tipo = new TypeToken<List<Perro>>() {}.getType();

            List<Perro> perros = gson.fromJson(json, tipo);
            return perros != null ? perros : new ArrayList<>();

        } catch (IOException e) {
            throw new RuntimeException("Error al cargar los perros.", e);
        }
    }

    @Override
    public void guardarPerros(List<Perro> perros) {
        try {
            String json = gson.toJson(perros);
            Files.writeString(archivoPerros, json);

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar los perros.", e);
        }
    }

    @Override
    public List<Gato> cargarGatos() {
        if (!Files.exists(archivoGatos)) {
            return new ArrayList<>();
        }

        try {
            String json = Files.readString(archivoGatos);
            Type tipo = new TypeToken<List<Gato>>() {}.getType();

            List<Gato> gatos = gson.fromJson(json, tipo);
            return gatos != null ? gatos : new ArrayList<>();

        } catch (IOException e) {
            throw new RuntimeException("Error al cargar los gatos.", e);
        }
    }

    @Override
    public void guardarGatos(List<Gato> gatos) {
        try {
            String json = gson.toJson(gatos);
            Files.writeString(archivoGatos, json);

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar los gatos.", e);
        }
    }
}
