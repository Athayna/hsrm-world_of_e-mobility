package de.hsrm.mi.swt_project.demo;

/**
 * Data Transfer Object that the Server receives by the Client on every game command by a user
 * @author Tom Gouthier
 */
public record GetGameCommandDTO(String user, GameControl control) {}

