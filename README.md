## An example liftweb app for async snippets

This is sample code for a blog-post I wrote [here](http://bhashitparikh.com/2016/07/17/futures-and-async-rendering-liftweb.html)

This is just a demo app that was developed using
[Lift 2.6 Final](http://liftweb.net/26) release, with one of the stock
templates named 'lift\_advanced\_bs3' that comes with the
[downloads](http://liftweb.net/download).

The app is a demo for rendering snippets in liftweb where you need to
deal with Futures, but don't want to use blocking code.

The code contains enough comments, and should be self-explanatory.

## Running The Code

This is standard Liftweb app, and can be run like any other such app.

1. Clone the repo: `git clone git@github.com:Bhashit/liftweb-async-snippet-sample.git`
2. `cd liftweb-async-snippet-sample`
3. `./sbt`
4. `container:start`

The app will be running at localhost:8080.
