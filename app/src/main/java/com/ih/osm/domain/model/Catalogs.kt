package com.ih.osm.domain.model

data class Catalogs(
    val cardTypes: List<CardType>,
    val priorities: List<Priority>,
    val preclassifiers: List<Preclassifier>,
    val levels: List<Level>,
    val employees: List<Employee>,
    val cards: List<Card>,
)
