# Documentation

The documentation website is written in Markdown and built using [Jekyll](https://jekyllrb.com/).
It is hosted on [GitHub Pages](http://pages.github.com/).

## Installation

In order to use Jekyll, you will need the following dependencies:
* [Ruby](https://www.ruby-lang.org)
* [RubyGems](https://rubygems.org/)
* [Bundler](http://bundler.io/)

Once you have installed Ruby, you can install Bundler using `gem`:
```shell
$ gem install bundler
```

**Note:** In OS X 10.11 you will need to add `-n /usr/local/bin` to the command above.

Assuming you have installed the dependencies, you can setup the project:

```shell
$ cd cuppa/docs
$ bundle install --path vendor/bundle
```

## Development

To test the website locally, use Jekyll to serve the website:

```shell
$ cd cuppa/docs
$ bundle exec jekyll serve -w
```

## Deployment

Begin by cloning this repository a second time and checking out the `gh-pages` branch. Assuming the second repository
is called `cuppa-gh-pages`, you can build the site directly into the repository:

```shell
$ cd cuppa/docs
$ bundle exec jekyll build -d ../../cuppa-gh-pages
$ cd ..
$ ./gradlew javadoc
$ cp -r cuppa/build/docs/javadoc/ ../cuppa-gh-pages/javadoc/cuppa
$ cp -r cuppa-junit/build/docs/javadoc/ ../cuppa-gh-pages/javadoc/cuppa-junit
```

Pushing `gh-pages` to GitHub will deploy the new content to the website. This should be done only when a new release
has been made.