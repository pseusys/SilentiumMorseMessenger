package com.ekdorn.silentium.morse

class Message {
    enum class Atom (val bin: Int) { DIT(0b1), DASH(0b11), GAP(0b0), CHARACTER(0b10), LINE(0b00) }


}
