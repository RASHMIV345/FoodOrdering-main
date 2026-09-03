package org.example.service;

import org.example.model.FoodItem;
import org.example.repository.FoodItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;

    public FoodItemService(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    public List<FoodItem> getAllFoodItems() {
        return foodItemRepository.findAll();
    }

    public Optional<FoodItem> getFoodItemById(Integer id) {
        return foodItemRepository.findById(id);
    }

    public FoodItem addFoodItem(FoodItem foodItem) {
        return foodItemRepository.save(foodItem);
    }

    public FoodItem updateFoodItem(Integer id, FoodItem updatedFood) {

        FoodItem existingFood = foodItemRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Food item not found"));

        existingFood.setName(updatedFood.getName());
        existingFood.setPrice(updatedFood.getPrice());

        return foodItemRepository.save(existingFood);
    }

    public void deleteFoodItem(Integer id) {
        foodItemRepository.deleteById(id);
    }
}