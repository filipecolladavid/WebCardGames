/**
 * <h1>Package wcg.main</h1>
Classes for managing the web card game service.
See: Description

Class Summary
Class					Description
GamePool				Pool of games managed by their ID.
Manager					An instance of this class is responsible for managing users and game instances.
RealManagerTest	 
User					A system user, with a user and password.
UserPool				A pool of system users with methods to add and retrieve them.


<h2>Package wcg.main Description</h2>
Classes for managing the web card game service. Manager is the main class and exposes methods called remotely by the web clients. It has a single instance that provides a single access point to this package, thus following both the Singleton and Facade design patterns.

The rest of the classes manage users and game instances. The UserPool class manages users and is serializable, thus ensuring that user data is persisted and can be recovered if the system is restarted.


Author:
José Paulo Leal jpleal@fc.up.pt
 */
package wcg.main;