package com.monrabal.springboot.reactor.app;

import com.monrabal.springboot.reactor.app.models.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		ejemploCollectList();
	}

	public void ejemploCollectList() throws Exception {

		List<Usuario> usuariosLst = new ArrayList<>();
		usuariosLst.add(new Usuario("Vicente", "Monrabal"));
		usuariosLst.add(new Usuario("Andrés", "Fulano"));
		usuariosLst.add(new Usuario("Maria", "Fulana"));
		usuariosLst.add(new Usuario("Diego", "Sultano"));
		usuariosLst.add(new Usuario("Alberto", "Liao"));
		usuariosLst.add(new Usuario("Bruce", "Lee"));
		usuariosLst.add(new Usuario("Bruce", "Willis"));

		Flux.fromIterable(usuariosLst)
				.collectList()
				.subscribe(lista -> {
					lista.forEach(item -> log.info(item.toString()));
				});
	}

	public void ejemploToString() throws Exception {

		List<Usuario> usuariosLst = new ArrayList<>();
		usuariosLst.add(new Usuario("Vicente",  "Monrabal"));
		usuariosLst.add(new Usuario("Andrés",  "Fulano"));
		usuariosLst.add(new Usuario("Maria",  "Fulana"));
		usuariosLst.add(new Usuario("Diego",  "Sultano"));
		usuariosLst.add(new Usuario("Alberto",  "Liao"));
		usuariosLst.add(new Usuario("Bruce",  "Lee"));
		usuariosLst.add(new Usuario("Bruce",  "Willis"));

		Flux.fromIterable(usuariosLst)
				.map(usuario -> usuario.getNombre().toUpperCase().concat(" ").concat(usuario.getApellido().toUpperCase()))
				.flatMap(nombre -> {
					if(nombre.contains("bruce".toUpperCase())){
						return Mono.just(nombre);
					}
					else{
						return Mono.empty();
					}
				})
				.map(nombre -> {
					return nombre.toLowerCase();
				}).subscribe(u -> log.info(u.toString())
		);
	}

	public void ejemploFlatmap() throws Exception {

		List<String> usuariosLst = new ArrayList<>();
		usuariosLst.add("Vicente Monrabal");
		usuariosLst.add("Andrés Fulano");
		usuariosLst.add("Maria Fulana");
		usuariosLst.add("Diego Sultano");
		usuariosLst.add("Alberto Liao");
		usuariosLst.add("Bruce Lee");
		usuariosLst.add("Bruce Willis");

		Flux.fromIterable(usuariosLst)
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.flatMap(usuario -> {
					if(usuario.getNombre().equalsIgnoreCase("bruce")){
						return Mono.just(usuario);
					}
					else{
						return Mono.empty();
					}
				})
				.map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				}).subscribe(u -> log.info(u.toString())
		);
	}


	public void ejemploIterable() throws Exception {

		List<String> usuariosLst = new ArrayList<>();
		usuariosLst.add("Vicente Monrabal");
		usuariosLst.add("Andrés Fulano");
		usuariosLst.add("Maria Fulana");
		usuariosLst.add("Diego Sultano");
		usuariosLst.add("Alberto Liao");
		usuariosLst.add("Bruce Lee");
		usuariosLst.add("Bruce Willis");


		Flux<String> nombres = Flux.fromIterable(usuariosLst);
		//Flux.just("Vicente Monrabal", "Andrés Fulano", "Maria Fulana",
		//"Diego Sultano", "Alberto Liao", "Bruce Lee", "Bruce Willis");

		Flux<Usuario> usuarios = nombres.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.filter(usuario -> usuario.getNombre().toLowerCase().equals("bruce"))
				.doOnNext(usuario -> {
					if(usuario == null){
						throw new RuntimeException("Nombres no pueden estar vacíos");
					}
					System.out.println(usuario.getNombre()+ " " + usuario.getApellido());
				})
				.map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				});

		usuarios.subscribe(e -> log.info(e.toString()),
				error -> log.error(error.getMessage()),
				new Runnable() {
					@Override
					public void run() {
						log.info("Ha finalizado la ejecución del observable");
					}
				});
	}
}
