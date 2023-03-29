# Apple Notes Liberator

Free your Apple Notes data from Notes.app. Currently only extracts Note text and tables, but support for extracting more types of data is planned.

> Note: This project is in early development. Bugs are sure to be lurking. Please [open an issue][0] or a [pull request][1] should you encounter one! Also feel free to do the same if you would like to request a feature.

## Credit where credit is due

This project would absolutely not be possible without the incredibly difficult and amazing work by [threeplanetssoftware][2]. Their code, [their protobuf definitions][3], and [their blog posts regarding Apple Notes][4], were invaluable. There's no way I would have been able to figure out any of the Apple Notes structure without it. Furthermore, their tool supports much more than mine! So if you want a more feature-rich Notes extractor, use theirs.

## Motivations

- To "scratch my own itch" of getting data out of Apple Notes so I can use and edit it other places
- Learn more about Quarkus

## Usage

This is a java command line application, and therefore you will need to have Java installed on your system such that you can run `java -version` from your terminal of choice.

Afer ensuring you have a JRE on your system, download a release jar from the [releases][5] page. Execute the program by running `java -jar apple-notes-liberator.jar`.

This application will attempt to locate the notes database on your computer, copy it, and parse what it can. If the application cannot locate your notes database, it will print an error to the terminal and exit. In that case, you can specify the `-f` or `--file` option, passing the path to the notes database you want the application to extract data from. 

If the program exits with no output to the terminal, then everything should have gone well and you should have a `notes.json` file in the same directory from which you executed the program.

If you get the following error and using external program like ITerm, check the permission to access folder in the program. You can add a "full disk access" in Mac settings to update permissions.

**This applicaiton does NOT perform any sort of read or modification operation on your actual notes database, rather it makes a copy of it and reads from its copy.**

## Sandbox Permissions

In MacOS 10.13 and later the storage location for the notes database is protected by Apple's security sandbox.  If you are using a third party terminal  such as iTerm, you may receive an error like this when running this application:

```
Cannot copy notes database, do you have read and execute permissions for /Users/xxx/Library/Group Containers/group.com.apple.notes/notestore.sqlite?
```

To allow access you will need to grant Full Disk Access to your terminal app by opening Control Panel->Privacy and Security->Full Disk Access and enabling the option for you terminal app.

<img width="701" alt="image" src="https://user-images.githubusercontent.com/3091714/228573691-60ce13cf-d5f1-46a1-a740-ef2f14786916.png">



## Output format

Currently, the only supported output format is json, though support for html and csv is planned.

The `notes.json` file will contain an array of objects, where each object represents an Apple Note.
Each object will have a `text` and `embeddedObjects` property. The `text` property will contain the extracted plain text from the note, and the `embedddObjects` property will contain a list of the embedded note objects that were extracted.

Each object in the the array represents an extracted Apple Note and will contain the following fields:

| Field Name | Description |
| --- | --- |
| `text` | The plain text extracted from the note |
| `embeddedObjects` | A list of the embedded note objects that were extracted from the note |

Each item in the `embeddedObjects` list will contain at minimun the following fields:

| Field Name | Description |
| ---  | --- |
| `type`       | The type of embedded object. Currently, only embedded tables are extracted so this field will always contain the value `TABLE`. As support for extraction of more embedded object types is added, this field will indicate their type. |
| `data` | [Embedded object data representation][6] |

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

[0]: https://github.com/HamburgChimps/apple-notes-liberator/issues
[1]: https://github.com/HamburgChimps/apple-notes-liberator/pulls
[2]: https://github.com/threeplanetssoftware/apple_cloud_notes_parser
[3]: https://github.com/HamburgChimps/apple-notes-liberator/blob/main/src/main/proto/notestore.proto
[4]: https://www.ciofecaforensics.com/categories/#Apple%20Notes
[5]: https://github.com/hamburgchimps/apple-notes-liberator/releases
[6]: #embedded-object-data-representation
