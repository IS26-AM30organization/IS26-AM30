/**
 * Package for Client implementation.
 * <br/>This Package contains the Client-side communication and logic of the Game "Mesos"; it defines the Client entry point,
 * as well as the full Client implementation, divided into two sub-Packages:
 * <ul>
 *     <li>{@link mesos.am30.client.gui}: detailed scene definition for the GUI.</li>
 *     <li>{@link mesos.am30.client.view}: implementation of the View and its communication logic.</li>
 * </ul>
 * <br/>In this Package have been implemented also the View User Interface, based on both a TUI and GUI design.
 * <br/>The View specialization on multiple dimensions (communication - User Interface) has been implemented following the <strong>Bridge</strong> Pattern,
 * using an abstract Class {@link mesos.am30.client.view.VirtualView} for the communication and an Interface {@link mesos.am30.client.IF_GameUI} for the User Interface.
 * <br/>The communication logic supports the following communication protocols:
 * <ul>
 *     <li>Socket.</li>
 *     <li>RMI.</li>
 * </ul>
 * The User Interface has been designed in the following adaptations:
 * <ul>
 *     <li>TUI.</li>
 *     <li>GUI.</li>
 * </ul>
 */
package mesos.am30.client;