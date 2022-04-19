graph =  {"A": {"G", "B", "H"},
          "G": {"A", "B"},
          "B": {"A", "G", "H", "C"},
          "H": {"A", "B"},
          "C": {"B", "E", "F", "D"},
          "E": {"C", "D"},
          "F": {"C", "D"},
          "D": {"C", "E", "F"}}


def all_trails(start_node, end_node, path, edges):
    path = path + [start_node]
    paths = []

    if start_node == end_node:
        return [path]

    for node in graph[start_node]:
        edge = {start_node, node}

        if edge not in edges:
            edges.append(edge)
            to_add = all_trails(node, end_node, path, edges)

            for p in to_add:
                paths.append(p)
        
    
    return paths


def all_paths(start_node, end_node, path):
    path = path + [start_node]

    if start_node == end_node:
        return [path]

    paths = []
    for node in graph[start_node]:
        if node not in path:
            to_add = all_paths(node, end_node, path)

            for p in to_add:
                paths.append(p)

    return paths


def format_out(paths):
    out = "{"
    for path in paths:
        # print on format "abc"
        out += ('"' + str("".join(i for i in path)) + '"')
        out += ","

    print(out)


def main():
    #p = all_paths("G", "F", [])
    #format_out(p)
    t = all_trails("A", "E", [], [])
    print(t)
    #format_out(t)


if __name__ == "__main__":
    main()
