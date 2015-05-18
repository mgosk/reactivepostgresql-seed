# reactivepostgresql-seed

## Introduction

This project is an application skeleton for a typical rest api. It is writen in Scala and uses akka-http and new reactive slick.
You can use it to quickly bootstrap your webapp projects and dev environment for these projects.

The seed app doesn't do much, just handle authentication and shows how to wire few service together.

## Reactive

Few words why `be reactive` is so cool

# Getting Started

To get you started you can simply clone the reactivepostgresql-seed repository, init database and run application

### Prerequisites

You need git to clone the reactivepostgresql-seed repository. You can get git from http://git-scm.com/.

### Clone angular-seed

Clone the reactivepostgresql-seed repository using git:

    git clone https://github.com/mgosk/reactivepostgresql-seed
    cd angular-seed

If you just want to start a new project without the angular-seed commit history then you can do:

    git clone --depth=1 https://github.com/mgosk/reactivepostgresql-seed <your-project-name>

The depth=1 tells git to only pull down one commit worth of historical data.

### Run the Application

We preconfigured for you. The simplest way to start this server in development mode:

    sbt start

Now to can make calls to your app.

### Endpoints


### Roadmap 

* Unit tests
* Rewrite notes to use postgresql json storage
* Use cache (maybe redis)
* Configure `sbt-native-packager` to compile docker images
* Configure `deploy to heroku` button
* Improve registration emails