![GitHub](https://img.shields.io/github/license/VincenzoArceri/rust-lisa)
![GitHub last commit](https://img.shields.io/github/last-commit/VincenzoArceri/rust-lisa)

![Rust](https://img.shields.io/badge/rust-%23000000.svg?style=style-flat-black&logo=rust&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=style-flat&logo=java&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=style-flat&logo=Gradle&logoColor=white)
# RustLiSA
A Static Analyzer for both [Rust](https://www.rust-lang.org/) and [Unsafe](https://doc.rust-lang.org/book/ch19-01-unsafe-rust.html) Rust built using [LiSA](https://unive-ssv.github.io/lisa/)

# 🛠 Build
RustLiSA comes as a [Gradle 6.6](https://gradle.org/releases/) project.

Development is done in [Eclipse](https://www.eclipse.org/downloads/).
You need to:
- install the Gradle IDE Pack plugin through the Eclipse Marketplace; from the eclipse menu bar:
  - *Help*
  - *Eclipse Marketplace...*
  - *Search* for *Gradle IDE Pack 3.8*
  - *Install Gradle IDE Pack 3.8*
- import the project into the eclipse workspace as a Gradle project.
- run the `./rust-lisa/gradlew build`

Run the project with [Java 11](https://www.oracle.com/it/java/technologies/javase/jdk11-archive-downloads.html). **Note:** in order to have gradle run you must run the project necessary with Java 11 **and no greater version**.

# ⚙️ Run
To run the project by using Eclipse you can use *Run as Java application* in Eclipse and passing a path to a Rust file as first argument.

The result will be put under the `output` folder, which will have all the enabled reports and a [.dot](https://en.wikipedia.org/wiki/DOT_(graph_description_language)) file with the [LiSA-CFG](https://unive-ssv.github.io/lisa/)

You can run other gradle task with
```bash
./rust-lisa/gradlew -q :tasks --all
```

# 🎯 Contribute
Before every commit it is necessary to open a new branch with
```bash
git checkout -b <branch-name>
```
then you can start committing on that branch.
Once you have finished, you first need to run
```bash
./rust-lisa/gradlew build
./rust-lisa/gradlew spotlessApply
```
and commit every change made to the files.

After that, you can open a Pull Request, marking the reviewers and adding tags about the PR, also with a short description.
