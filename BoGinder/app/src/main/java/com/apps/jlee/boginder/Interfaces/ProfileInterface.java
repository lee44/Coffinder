package com.apps.jlee.boginder.Interfaces;

import com.apps.jlee.boginder.Models.Card;

public class ProfileInterface
{
    public interface ProfileCallback
    {
        void loadUI(Card card);
    }
}
