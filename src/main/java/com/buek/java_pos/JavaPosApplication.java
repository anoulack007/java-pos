package com.buek.java_pos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class JavaPosApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaPosApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		String reset = "\u001B[0m";
		String green = "\u001B[32m";
		String cyan = "\u001B[36m";
		String yellow = "\u001B[33m";
		
		System.out.println("\n");
		System.out.println(green + "╔════════════════════════════════════════════════════════════╗" + reset);
		System.out.println(green + "║" + reset + "                                                            " + green + "║" + reset);
		System.out.println(green + "║" + reset + "          " + yellow + "✓ Application Started Successfully" + reset + "               " + green + "║" + reset);
		System.out.println(green + "║" + reset + "                                                            " + green + "║" + reset);
		System.out.println(green + "║" + reset + "                                                            " + green + "║" + reset);
		System.out.println(green + "║" + reset + "  " + cyan + "📊 Database:" + reset + " postgresql://localhost:5432/posdb           " + green + "║" + reset);
		System.out.println(green + "║" + reset + "                                                            " + green + "║" + reset);
		System.out.println(green + "║" + reset + "                                                            " + green + "║" + reset);
		System.out.println(green + "║" + reset + "  " + cyan + "🌐 Server:" + reset + "   http://localhost:8081                       " + green + "║" + reset);
		System.out.println(green + "║" + reset + "                                                            " + green + "║" + reset);
		System.out.println(green + "║" + reset + "                                                            " + green + "║" + reset);
		System.out.println(green + "║" + reset + "  " + cyan + "📝 API:" + reset + "      http://localhost:8081/api                    " + green + "║" + reset);
		System.out.println(green + "║" + reset + "                                                            " + green + "║" + reset);
		System.out.println(green + "║" + reset + "                                                            " + green + "║" + reset);
		System.out.println(green + "╚════════════════════════════════════════════════════════════╝" + reset);
		System.out.println("\n");
	}

}
