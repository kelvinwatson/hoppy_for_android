package com.iamhoppy.hoppy;

import java.util.Observable;
import java.util.Observer;

public class ObserverPattern {

    public class PetObserver implements Observer {
        public PetObserver(){

        }
        @Override
        public void update(Observable observable, Object data) {
            System.out.println("PetObserver observed a change!");
        }
    }

    public class Pet extends Observable {
        private boolean isHungry;
        public Pet(boolean isHungry){
            this.isHungry = isHungry;
        }

        public void setIsHungry(boolean isHungry) {
            this.isHungry = isHungry;
        }
        @Override
        protected void setChanged() {
            super.setChanged();
        }
    }

    public void main(String[] args){
        PetObserver po = new PetObserver();
        Pet p = new Pet(false);
        p.addObserver(po);
        p.setIsHungry(true);
        p.setChanged();
        p.notifyObservers();

    }

}
