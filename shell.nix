{ pkgs ? import <nixpkgs> {} }:

let
  apple-notes-liberator = pkgs.fetchurl {
    url = "https://github.com/HamburgChimps/apple-notes-liberator/releases/download/v2.2.2/apple-notes-liberator.jar";
    sha256="sha256-SKbwdqQ5O9sSmgZFgYB6D4y/qJe5NwEwy3zkG4GYU+Y=";
  };
in pkgs.mkShell {
  buildInputs = with pkgs; [
    pkgs.openjdk19
  ];

  shellHook = ''
      cp ${apple-notes-liberator} .
      alias apple-notes-liberator='java -jar ${apple-notes-liberator}'
      echo "Usage: apple-notes-liberator"
    '';
}
