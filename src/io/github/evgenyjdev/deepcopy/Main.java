package io.github.evgenyjdev.deepcopy;

import io.github.evgenyjdev.deepcopy.test.Man;
import io.github.evgenyjdev.deepcopy.test.ManWithNoArgConstructor;
import io.github.evgenyjdev.deepcopy.test.SophisticatedMan;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println("Java version: " + System.getProperty("java.version"));

        Cloner cloner = new Cloner();

        ManWithNoArgConstructor manWithNoArgConstructor = new ManWithNoArgConstructor("Bob", 30, Arrays.asList("1st book", "2nd book", "3rd book"));
        ManWithNoArgConstructor manWithNoArgConstructorCopy = cloner.deepCopy(manWithNoArgConstructor);
        System.out.println("\n---ManWithNoArgConstructor---");
        System.out.println("names 'equals': " + manWithNoArgConstructor.getName().equals(manWithNoArgConstructorCopy.getName()));
        System.out.println("names '==' " + (manWithNoArgConstructor.getName() == manWithNoArgConstructorCopy.getName()));
        // Сlasses without a no-arg constructor are not cloned:
        System.out.println("manWithNoArgConstructorCopy.favoriteBooks = " + manWithNoArgConstructorCopy.getFavoriteBooks());

        System.out.println("\n---Man---");
        Man man = new Man("John", 30, Arrays.asList("1st book", "2nd book", "3rd book"));
        cloner.setForceInstantiate(true); // In this mode, the Сloner tries to create an instance using random constructor:
        Man manCopy = cloner.deepCopy(man);
        System.out.println("favoriteBooks 'equals': " + man.getFavoriteBooks().equals(manCopy.getFavoriteBooks()));
        System.out.println("favoriteBooks '==': "  + (man.getFavoriteBooks() == manCopy.getFavoriteBooks()));

        System.out.println("\n---SophisticatedMan---");
        SophisticatedMan sophisticatedMan = new SophisticatedMan("Timothy", 'a', Arrays.asList("1st book", "2nd book", "3rd book", "4th book"));
        SophisticatedMan sophisticatedManCopy = cloner.deepCopy(sophisticatedMan);
        System.out.println("SophisticatedMan var5: " + Arrays.toString(sophisticatedMan.getVar5()));
        System.out.println("SophisticatedManCopy var5: " + Arrays.toString(sophisticatedManCopy.getVar5()));
        System.out.println("var5 '==' " + (sophisticatedMan.getVar5() == sophisticatedManCopy.getVar5()));
    }
}