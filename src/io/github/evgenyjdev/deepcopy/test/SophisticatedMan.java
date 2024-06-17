package io.github.evgenyjdev.deepcopy.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SophisticatedMan {
    private String name;
    private char age;
    private int var1;
    private byte var2;
    private short var3;
    private float var4;
    private int[] var5 = {1, 2, 3, 4};

    public int[] getVar5() {
        return var5;
    }

    boolean var6 = true;
    private List<String> favoriteBooks;
    SophisticatedMan self;
    private List<SophisticatedMan> neighbours;
    BigDecimal bd = BigDecimal.valueOf(10L);

    public SophisticatedMan(String name, char age, List<String> favoriteBooks) {
        this.name = name;
        this.age = age;
        // breaks cloning!
        //     if (favoriteBooks == null) {
        //         throw  new RuntimeException("fkewfjewi");
        //     }
        this.favoriteBooks = favoriteBooks;
        self = this;
        neighbours = new ArrayList<>(Arrays.asList(self));
    }

    public SophisticatedMan(String name, char age, List<String> favoriteBooks, int var1, byte var2, short var3, float var4, int[] var5, boolean var6) {
        this.name = name;
        this.age = age;
        this.favoriteBooks = favoriteBooks;
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.var6 = var6;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getAge() {
        return age;
    }

    public void setAge(char age) {
        this.age = age;
    }

    public List<String> getFavoriteBooks() {
        return favoriteBooks;
    }

    public void setFavoriteBooks(List<String> favoriteBooks) {
        this.favoriteBooks = favoriteBooks;
    }

}
