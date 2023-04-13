# Apple Notes Liberator

Free your Apple Notes data from Notes.app.

## Credit where credit is due

This project would absolutely not be possible without the incredibly difficult and amazing work by [threeplanetssoftware][2]. Their code, [their protobuf definitions][3], and [their blog posts regarding Apple Notes][4], were invaluable. There's no way I would have been able to figure out any of the Apple Notes structure without it. Furthermore, their tool supports much more than mine! So if you want a more feature-rich Notes extractor, use theirs.

## Motivations

- To "scratch my own itch" of getting data out of Apple Notes so I can use and edit it other places
- Learn more about Quarkus

## Usage

### With JBang

Perhaps the most comfortable way to use this application is install it with [JBang][5]. Jbang is a really cool tool that enables easy execution of java applications without having to hassle with manually downloading jars or compiling yourself first.

For example, if you have JBang installed, you can install `apple-notes-liberator` by executing the following command:

```bash
jbang app install apple-notes-liberator@hamburgchimps
```

You will then be greeted with the following message:

```bash
[jbang] https://github.com/hamburgchimps/apple-notes-liberator/releases/latest/download/apple-notes-liberator.jar is not from a trusted source thus not running it automatically.

If you trust the url to be safe to run you can do one of the following

(1) Trust once: Add no trust, just download this time (can be run multiple times while cached)
(2) Trust limited url in future: https://github.com/hamburgchimps/apple-notes-liberator/releases/latest/download/
(3) Trust organization url in future: https://github.com/hamburgchimps/apple-notes-liberator/releases/latest/
(0) Cancel

[jbang] Type in your choice and hit enter. Will automatically select option (0) after 30 seconds.
```

Select option `2`, which will allow you to always execute the latest version of `apple-notes-liberator`.

You should then see the following output in your terminal:

```bash
[jbang] Adding [https://github.com/hamburgchimps/apple-notes-liberator/releases/latest/download/] to /Users/YOUR_USER/.jbang/trusted-sources.json
[jbang] Command installed: apple-notes-liberator
```

Run `apple-notes-liberator --help` from your terminal. You should see the following output:

```bash
Usage: <main class> [-hjmV] [-f=<noteStoreDb>]
Free your data from Apple Notes.
  -f, --file=<noteStoreDb>   Path to Apple Notes sqlite file
  -h, --help                 Show this help message and exit.
  -j, --json                 Generate JSON
  -m, --markdown             Generate markdown (in early development, please
                               report bugs and request features here -> https:
                               //github.
                               com/HamburgChimps/apple-notes-liberator/issues)
  -V, --version              Print version information and exit.
```

You're now ready to use the application!

Running the application via JBang in this manner has the added benefit that you will always be running the latest version.

### With Nix Shell

If you have nix installed on your computer, you can run `nix-shell` which will download a JAR from the releases page and put you into an isolated shell with Java 19.

From there you can run `apple-notes-liberator`.

### Downloading and executing a release jar

If you don't want to use JBang or don't use Nix shell, then you will need to already have Java installed on your system such that you can run `java -version` from your terminal of choice. Then, download a release jar from the [releases][6] page. Execute the program by running `java -jar apple-notes-liberator.jar`.

## What does this application do?

This application will attempt to locate the notes database on your computer, copy it, and parse what it can. If the application cannot locate your notes database, it will print an error to the terminal and exit. In that case, you can specify the `-f` or `--file` option, passing the path to the notes database you want the application to extract data from.

The application will generate JSON and/or markdown from your Notes.app data depending on the options you specify. You must specify at least one of either `-j (--json)` or `-m (--markdown)` for the application to do anything. If you do not specify at least one of these options, the application will output its usage information to the terminal and exit.

If the program exits with no output to the terminal, then everything should have gone well and you should have a `liberated-notes` directory inside of the directory from which you executed the program. The `liberated-notes` directory will contain the following things depending on the options you provided:

- A `notes.json` file. This is a JSON representation of all extracted notes. This is present when the `-j` or `--json` option was specified.
See [JSON Output Format][7] for more information on how this JSON is structured.

- A `markdown/` directory containing a markdown file for each note in your Notes.app database. This is present when the the `-m` or `--markdown` option was specified.
- Copies of all embedded files that could be extracted from your notes data. These will be referenced in the `notes.json` file and the generated markdown files.
- A `notes.sqlite` file. This is a copy of your Notes.app SQLite database.

Note that when using a third-party terminal app, such as iTerm, you may need to enable "Full Disk Access", otherwise the program will probably exit with the following error:

> Cannot copy notes database, do you have read and execute permissions for /Users/xxx/Library/Group Containers/group.com.apple.notes/notestore.sqlite?

This is because in MacOS 10.13 and later, the storage location for the notes database is protected by Apple's security sandbox.

To grant "Full Disk Access" to your terminal app, open up the Control Panel->Privacy and Security->Full Disk Access window and enable it for you terminal app:

<img width="701" alt="image" src="https://user-images.githubusercontent.com/3091714/228573691-60ce13cf-d5f1-46a1-a740-ef2f14786916.png">

**This application does NOT perform any sort of read or modification operation on your actual notes database, rather it makes a copy of it and reads from its copy.**

### Enable Logging

If something isn't working, it might help to see what the application is doing. You can do so by setting the environment variable `QUARKUS_PROFILE=debug` before running the application:

```bash
QUARKUS_PROFILE=debug apple-notes-liberator
```

If you didn't install the application with JBang, but instead downloaded a release jar, you can also use a JVM argument:

```bash
java -Dquarkus.profile=debug -jar apple-notes-liberator.jar
```

This will result in a log file named `apple-notes-liberator.log` being generated in the `liberated-notes` directory.

## JSON Output format

The `notes.json` file will contain an array of objects, where each object represents an Apple Note, and will contain the following fields:

| Field Name | Description |
| --- | --- |
| `title` | The title extracted from the note |
| `folder` | The name of the folder that the note is located in |
| `text` | The plain text extracted from the note |
| `embeddedObjects` | A list of the embedded note objects that were extracted from the note |
| `links` | A list of links that were extracted from the note. Each object in the `links` list has a `text` and `url` property. |

Each item in the `embeddedObjects` list will contain at minimum the following fields:

| Field Name | Description |
| ---  | --- |
| `type` | The type of embedded object, either `TABLE` or `FILE`. |
| `data` | [Embedded object data representation][8] |

## Embedded object data representation

The structure of the `data` field will vary depending on the type of the embedded object. This section describes the structure of the `data` field for each type of embedded object.

### Table

A two-dimensional array represending the rows and columns of the table. Each item in the outer array represents a row in the table, and each element of an inner array represents a column.

For example, say you had the following table in your original Apple Note:

| Animal | Cuteness Factor |
| --- | --- |
| Giraffe | 8 |
| Dog | 11 |
| Cat | 10 |

Then the `data` field would contain the following two-dimensional array:

```
[
  [ 'Animal', 'Cuteness Factor' ],
  [ 'Giraffe', '8' ],
  [ 'Dog', '11' ],
  [ 'Cat', '10' ]
]
```

### File

A string containing the name of the extracted file. You can then find this file in the `liberated-notes` directory.

For example, if the `data` field contained the string `"cat-pic.png"`, then you will find a file named `cat-pic.png` in the `liberated-notes` directory.

[0]: https://github.com/HamburgChimps/apple-notes-liberator/issues
[1]: https://github.com/HamburgChimps/apple-notes-liberator/pulls
[2]: https://github.com/threeplanetssoftware/apple_cloud_notes_parser
[3]: https://github.com/HamburgChimps/apple-notes-liberator/blob/main/src/main/proto/notestore.proto
[4]: https://www.ciofecaforensics.com/categories/#Apple%20Notes
[5]: https://www.jbang.dev
[6]: https://github.com/hamburgchimps/apple-notes-liberator/releases
[7]: #json-output-format
[8]: #embedded-object-data-representation
[9]: #what-does-this-application-do
