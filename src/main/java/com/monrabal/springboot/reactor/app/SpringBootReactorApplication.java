package com.monrabal.springboot.reactor.app;

import com.monrabal.springboot.reactor.app.models.Comentarios;
import com.monrabal.springboot.reactor.app.models.Usuario;
import com.monrabal.springboot.reactor.app.models.UsuarioComentarios;
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
		ejemploZipWithRangos();
	}

	public void ejemploZipWithRangos(){
		Flux<Integer> rangos = Flux.range(0, 4);

		Flux.just(1, 2, 3, 4)
				.map(i -> (i*2))
				.zipWith(rangos, (uno, dos) -> String.format("Primer Flux %d, Segundo Flux %d",uno, dos))
				.subscribe(texto -> log.info(texto));
	}

	public void ejemploUsuarioComentariosZipWith2(){
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("John", "Doe"));

		Mono<Comentarios> comentariosMono = Mono.fromCallable(() -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentario("Hola comment 1");
			comentarios.addComentario("Adios comment 2");
			comentarios.addComentario("Donde comment 3, comment 4");
			return comentarios;
		});

		Mono<UsuarioComentarios> usuConComments = usuarioMono
				.zipWith(comentariosMono)
				.map(tuple -> {
					Usuario u = tuple.getT1();
					Comentarios c = tuple.getT2();
					return new UsuarioComentarios(u, c);
				});

		usuConComments.subscribe(uc -> log.info(uc.toString()));
	}

	public void ejemploUsuarioComentariosZipWith(){
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("John", "Doe"));

		Mono<Comentarios> comentariosMono = Mono.fromCallable(() -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentario("Hola comment 1");
			comentarios.addComentario("Adios comment 2");
			comentarios.addComentario("Donde comment 3, comment 4");
			return comentarios;
		});

		Mono<UsuarioComentarios> usuConComments =
				usuarioMono.zipWith(comentariosMono, (u, c) -> new UsuarioComentarios(u, c));

		usuConComments.subscribe(uc -> log.info(uc.toString()));
	}

	public void ejemploUsuarioComentariosFlatMap(){
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("John", "Doe"));

		Mono<Comentarios> comentariosMono = Mono.fromCallable(() -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentario("Hola comment 1");
			comentarios.addComentario("Adios comment 2");
			comentarios.addComentario("Donde comment 3, comment 4");
			return comentarios;
		});

		usuarioMono.flatMap(u -> comentariosMono.map(c -> new UsuarioComentarios(u, c)))
				.subscribe(uc -> log.info(uc.toString()));
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
