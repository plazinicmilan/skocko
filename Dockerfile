FROM clojure
WORKDIR /usr/src/app
EXPOSE 8080
CMD ["lein", "run", "8080"]