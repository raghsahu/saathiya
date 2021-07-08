package com.prometteur.sathiya.model;

import com.prometteur.sathiya.beans.beanHobbyImage;
import com.prometteur.sathiya.beans.beanUserData;

public class HistoryDataModelObject extends ListObject {

        private beanUserData chatModel;
        private beanHobbyImage hobbyImageModel;

        public beanUserData getChatModel() {
            return chatModel;
        }

        public void setChatModel(beanUserData chatModel) {
            this.chatModel = chatModel;
        }

    public beanHobbyImage getHobbyImageModel() {
        return hobbyImageModel;
    }

    public void setHobbyImageModel(beanHobbyImage hobbyImageModel) {
        this.hobbyImageModel = hobbyImageModel;
    }

    @Override
        public int getType() {

                return TYPE_GENERAL;

        }
    }