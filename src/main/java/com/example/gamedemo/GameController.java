package com.example.gamedemo;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class GameController {

    private final AtomicInteger highScore = new AtomicInteger(0);

    @GetMapping("/config/{difficulty}")
    public ResponseEntity<?> getConfig(@PathVariable String difficulty) {

        GameConfig config = switch (difficulty.toLowerCase(Locale.ROOT)) {
            case "easy" ->
                new GameConfig("Spring Boot", "Easy", 30, 110, 0);

            case "medium" ->
                new GameConfig("Spring Boot", "Medium", 25, 75, 1600);

            case "hard" ->
                new GameConfig("Spring Boot", "Hard", 20, 50, 850);

            case "insane" ->
                new GameConfig("Spring Boot", "Insane", 15, 34, 450);

            default -> null;
        };

        if (config == null) {
            return ResponseEntity
                .status(404)
                .body(new ErrorResponse("Unknown difficulty"));
        }

        return ResponseEntity.ok(config);
    }

    @GetMapping("/message")
    public MessageResponse getMessage() {
        return new MessageResponse("Hello from Spring Boot!");
    }

    @PostMapping("/score")
    public ResponseEntity<?> saveScore(@RequestBody ScoreRequest request) {

        if (request.name() == null || request.name().isBlank()) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("name is required"));
        }

        highScore.updateAndGet(
            current -> Math.max(current, request.score())
        );

        System.out.printf(
            "Score received -> player=%s, score=%d, difficulty=%s%n",
            request.name(),
            request.score(),
            request.difficulty()
        );

        return ResponseEntity.ok(
            new ScoreResponse(
                "Great job, " + request.name() +
                "! Your score was saved by Spring Boot.",
                highScore.get()
            )
        );
    }

    public record GameConfig(
        String backend,
        String difficulty,
        int gameTime,
        int targetSize,
        int targetSpeed
    ) {}

    public record ScoreRequest(
        String name,
        int score,
        String difficulty
    ) {}

    public record ScoreResponse(
        String message,
        int highScore
    ) {}

    public record MessageResponse(String message) {}

    public record ErrorResponse(String message) {}
}
