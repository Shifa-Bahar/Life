package com.lifepharmacy.application.enums

enum class ScrollingState {
  ON_SCROLL(0),
  SCROLL_DOWN(1),
  SCROLL_UP(2),
  HIT_BOTTOM(3),
  DEFAULT();

  private var id = 0

  constructor(id: Int) {
    this.id = id
  }

  constructor()

  companion object {
    fun fromId(id: Int): ScrollingState? {
      for (type in values()) {
        if (type.id == id) {
          return type
        }
      }
      return null
    }
  }
}