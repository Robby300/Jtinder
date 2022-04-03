package com.jtinder.application.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.jtinder.application.domen.Sex;
import com.jtinder.application.domen.User;
import com.jtinder.application.domen.Views;
import com.jtinder.application.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RequestMapping("users")
@RestController
public class UserRestController {

    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @JsonView(Views.Anketa.class)
    public User findById(@PathVariable(value = "id") User user) {
        logger.info("Обработка запроса /{} и получение пользователя по id.", user.getUserId());
        return user;
    }

    @GetMapping("/exists/{id}")
    public boolean isUserExistById(@PathVariable(value = "id") Long userId) {
        logger.info("Обработка запроса /exists/{} и получение пользователя по id.", userId);
        return userService.isExists(userId);
    }

    @GetMapping("/reciprocity/{id}")
    public boolean isReciprocity(@PathVariable(value = "id") User user) {
        logger.info("Обработка GET запроса /reciprocity/{} - достигнута ли взаимность.", user.getUserId());
        return userService.isReciprocity(user);
    }

    @GetMapping("/search")
    @JsonView(Views.Anketa.class)
    public List<User> searchUsers() {
        logger.info("Обработка запроса /search и получение всех доступных анкет.");
        return userService.searchUsers();
    }

    @GetMapping("/welike")
    @JsonView(Views.Anketa.class)
    public Set<User> findAllWeLike() {
        logger.info("Обработка GET запроса /welike - список кому мы поставили лайк.");
        return userService.findAllWeLike();
    }

    @GetMapping("/uslike")
    @JsonView(Views.Anketa.class)
    public Set<User> findAllUsLike() {
        logger.info("Обработка GET запроса /uslike - список кто нам поставил лайк.");
        return userService.findAllUsLike();
    }

    @GetMapping("/likers")
    @JsonView(Views.Anketa.class)
    public Set<User> findAllLikers() {
        Set<User> likers = new HashSet<>(userService.findAllWeLike());
        likers.addAll(userService.findAllUsLike());
        logger.info("Обработка GET запроса /likers - список лайкеров из {} элементов", likers.size());
        return likers;
    }

    @GetMapping("/reciprocity")
    @JsonView(Views.Anketa.class)
    public Set<User> findAllReciprocity() {
        logger.info("Обработка GET запроса /reciprocity - список взаимных лаков.");
        return userService.findAllReciprocity();
    }

    @PutMapping("/{id}")
    public User update(@PathVariable("id") User userFromDb,
                       @RequestBody User user) {
        BeanUtils.copyProperties(user, userFromDb, "id");
        logger.info("Обработка запроса /{} - обновление данных.", user.getUserId());
        return userService.save(userFromDb);
    }

    @PutMapping("/unlike/{user}")
    @JsonView(Views.Anketa.class)
    public User setUnLike(@PathVariable User user) {
        userService.unlike(user);
        logger.info("Обработка PUT запроса /unlike/{} - убрать лайк у пользователю.", user.getUserId());
        return user;
    }

    @PutMapping("/like/{user}")
    @JsonView(Views.Anketa.class)
    public User setLike(@PathVariable User user) {
        userService.like(user);
        logger.info("Обработка PUT запроса /unlike/{} - постваить лайк пользователю.", user.getUserId());
        return user;
    }

    @PostMapping()
    @JsonView(Views.Anketa.class)
    public User saveUser(@RequestBody User user) {
        logger.info("Обработка POST запроса {} - обновление данных.", user.getUserId());
        return userService.save(user);
    }

    // Временные методы
    @GetMapping("/imgdescr/{id}")
    public String getImageDescription(@PathVariable(value = "id") User user) {
        String resultString = user.getSex().getName() + ", " + user.getName();
        User currentUser = userService.getCurrentUser();
        if (userService.isReciprocity(user)) {
            return resultString + ", Взаимность";
        } else if (currentUser.getWeLike().contains(user)) {
            return resultString + ", Любим" + (user.getSex().equals(Sex.FEMALE)?"a":"") + " вами.";
        }  else if (currentUser.getUsLike().contains(user)) {
            return resultString + ", Вы любимы";
        }
        return resultString + ".";
    }

    /*@DeleteMapping("/{id}")
    public void delete(@PathVariable(value = "id") User user) {
        logger.info("Обработка DELETE запроса /{} - удалить пользователя.", user.getUserId());
        userService.deleteUser(user);
    }
    @GetMapping()
    @JsonView(Views.Anketa.class)
    public List<User> findAll() {
        return userService.findAll();
    }*/

/*    @GetMapping("/male")
    @JsonView(Views.Anketa.class)
    public List<User> findAllMale() {
        return userService.findAllMale();
    }

    @GetMapping("/female")
    @JsonView(Views.Anketa.class)
    public List<User> findAllFemale() {
        return userService.findAllFemale();
    }

    @GetMapping("/image/{id}")
    public void getImage(@PathVariable(value = "id") User user) {
        try {
            imageService.getFile(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
