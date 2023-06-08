{ pkgs ? import <nixpkgs> {} }:

let
  apple-notes-liberator = pkgs.fetchurl {
    url = "https://github.com/HamburgChimps/apple-notes-liberator/releases/download/v2.1.0/apple-notes-liberator.jar";
    sha256="sha256-vs/gu/TmwXR2nQkY6qDEwUN+QekXVGoRt9pE2OISY1s=";
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
