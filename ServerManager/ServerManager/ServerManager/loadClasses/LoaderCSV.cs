using ServerManager.data;
using System;
using System.Collections.Generic;

namespace ServerManager.Loader
{
    class LoaderCSV : AbstractLoader
    {
        public override List<UploadObject> Data => throw new NotImplementedException();
        public override int Entries => throw new NotImplementedException();
        public override int Tables => throw new NotImplementedException();
        public override string Path { get => throw new NotImplementedException(); set => throw new NotImplementedException(); }
        public override List<string> Tablenames => throw new NotImplementedException();

        public override void Load(IProgress<string> reporter)
        {
            throw new NotImplementedException();
        }
    }
}
