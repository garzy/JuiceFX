JuiceFX: afterburner.fx fork with Google Guice DI
=================================================

**note: this is a fork of the original project to add Google Guice DI** if you have better approach for this, let me know, thanks.

For default, default Guice injector without modules is created, if you want setup modules you must do this at the very first start time of your application:

```shell
Injector.setGuiceModules(new MyCustomModule1(), new MyCustomModule2());
```

It's all :)

## Known limitations
For now, injection of configuration.properties through primitive injection must also annotated by @Nullable

Example:

```shell
	@Inject
	@Nullable
   private String name;
```

## Important How To Import for now in your Maven Project:

While I'm learn and work in the way to upload to official Maven Central, do this for now in your pom.xml:

```xml
	<repositories>
    <repository>
        <id>JuiceFX-mvn-repo</id>
        <url>https://raw.github.com/garzy/JuiceFX/mvn-repo/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>

<dependencies>
 <dependency>
   <groupId>com.airhacks</groupId>
   <artifactId>juice.fx</artifactId>
   <version>1.7.1-SNAPSHOT</version>
 </dependency>
</dependencies>
```

=====================


The opinionated just-enough MVP framework (2.5 classes) for JavaFX

Afterburner is a "Just-Enough-Framework" extracted from [airhacks-control](https://github.com/AdamBien/airhacks-control) and used in [airpad](https://github.com/AdamBien/airpad), [lightfish](https://github.com/AdamBien/lightfish) and [floyd](https://github.com/AdamBien/floyd) applications. 

Goal: "Less Code, Increased Productivity"

Jumpstart with:

```shell
mvn archetype:generate -Dfilter=com.airhacks:igniter
```



Afterburner is also available from maven central:
```xml
        <dependency>
            <groupId>com.airhacks</groupId>
            <artifactId>afterburner.fx</artifactId>
            <version>[LATEST_RELEASE]</version>
        </dependency>
```
The current development version is available as snapshot:

[![igniter / afterburner walk-through](https://i1.ytimg.com/vi/xqkbu1IrHSw/mqdefault.jpg)](https://www.youtube.com/watch?v=xqkbu1IrHSw)

See also: [http://afterburner.adam-bien.com](http://afterburner.adam-bien.com)

Simplistic example:  [https://github.com/AdamBien/followme.fx](https://github.com/AdamBien/followme.fx)

Deploying afterburner.fx applications: [https://github.com/AdamBien/airfield/](https://github.com/AdamBien/airfield/)
