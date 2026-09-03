package org.example.controller;

import org.example.model.FoodItem;
import org.example.service.FoodItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
public class FoodController {

    private final FoodItemService foodItemService;

    public FoodController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    @GetMapping
    public ResponseEntity<List<FoodItem>> getAllFoodItems() {
        return ResponseEntity.ok(
                foodItemService.getAllFoodItems()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodItem> getFoodItem(
            @PathVariable Integer id) {

        return foodItemService.getFoodItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FoodItem> addFoodItem(
            @RequestBody FoodItem foodItem) {

        return ResponseEntity.ok(
                foodItemService.addFoodItem(foodItem)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodItem> updateFoodItem(
            @PathVariable Integer id,
            @RequestBody FoodItem foodItem) {

        return ResponseEntity.ok(
                foodItemService.updateFoodItem(id, foodItem)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodItem(
            @PathVariable Integer id) {

        foodItemService.deleteFoodItem(id);

        return ResponseEntity.noContent().build();
    }
}