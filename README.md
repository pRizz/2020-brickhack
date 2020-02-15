# generative-art-live

This is the generative-art-live project.

## Development mode

To start the Figwheel compiler, navigate to the project folder and run the following command in the terminal:

```
lein figwheel
```

Figwheel will automatically push cljs changes to the browser. The server will be available at [http://localhost:3449](http://localhost:3449) once Figwheel starts up. 

Figwheel also starts `nREPL` using the value of the `:nrepl-port` in the `:figwheel`
config found in `project.clj`. By default the port is set to `7002`.

The figwheel server can have unexpected behaviors in some situations such as when using
websockets. In this case it's recommended to run a standalone instance of a web server as follows:

```
lein do clean, run
```

The application will now be available at [http://localhost:3000](http://localhost:3000).


### Optional development tools

Start the browser REPL:

```
$ lein repl
```
The Jetty server can be started by running:

```clojure
(start-server)
```
and stopped by running:
```clojure
(stop-server)
```


## Building for release

```
lein do clean, uberjar
```

## Deploying to Heroku

Make sure you have [Git](http://git-scm.com/downloads) and [Heroku toolbelt](https://toolbelt.heroku.com/) installed, then simply follow the steps below.

Optionally, test that your application runs locally with foreman by running.

```
foreman start
```

Now, you can initialize your git repo and commit your application.

```
git init
git add .
git commit -m "init"
```
create your app on Heroku

```
heroku create
```

optionally, create a database for the application

```
heroku addons:add heroku-postgresql
```

The connection settings can be found at your [Heroku dashboard](https://dashboard.heroku.com/apps/) under the add-ons for the app.

deploy the application

```
git push heroku master
```

Your application should now be deployed to Heroku!
For further instructions see the [official documentation](https://devcenter.heroku.com/articles/clojure).

# README Forked from https://github.com/mwsundberg/2020-brickhack

# Brickhack 2020

I made this to explore functional programming as applied to generative art. I suspect it will be shortly abandoned, and I am certain that the code is of below average quality. However, here ya go!

## Usage

My process for interactively building this app was running `clojure -m figwheel.main -b dev` from the main directory and editing the namespace specified in `dev.cljs.edn`. I make no guarantees about this code, it is presented as is to the world. If you would like to take it and do with it as you see fit, feel free to do so (though I advise against it).

## Images
In this regard the app was quite successful. Below are some screenshots:

![Early Ddesign](pictures/2020-02-08-01.png) ![Ribbons with intersection](pictures/2020-02-09-07.png)
![Ribbons](pictures/2020-02-09-10.png)
![More early work](pictures/2020-02-08-02.png)
![Constrained angles](pictures/2020-02-08-03.png)
![Even ribbons](pictures/2020-02-09-01.png)
![Large step ribbons](pictures/2020-02-09-05.png)
![Short ribbons](pictures/2020-02-09.png)
![Misaligned ribbon baseline](pictures/2020-02-09-02.png)
![Closing lines](pictures/2020-02-09-03.png)
![Lower noise resolution](pictures/2020-02-09-04.png)
![Ribbons with intersection](pictures/2020-02-09-06.png)
![Spraypaint](pictures/2020-02-09-09.png)
![Interpolation](pictures/2020-02-09-08.png)
